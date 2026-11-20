package com.example.lekho.keyboard

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

internal data class HistoryPage(
    val total: Int,
    val wordsNewestFirst: List<String>
)

/**
 * Serial, background-only SQLite history store. The database keeps at most 20,000 words.
 * Existing SharedPreferences history is migrated once, preserving duplicate entries and order.
 */
internal object HistoryStore {
    private const val DATABASE_NAME = "likho_typing_history.db"
    private const val DATABASE_VERSION = 1
    private const val TABLE = "typing_history"
    private const val COLUMN_ID = "id"
    private const val COLUMN_WORD = "word"
    private const val COLUMN_CREATED_AT = "created_at"
    private const val PREFS_NAME = "lekho_local"
    private const val PREF_HISTORY = "history"
    private const val PREF_HISTORY_ENABLED = "history_enabled"
    private const val PREF_MIGRATED = "history_sqlite_migrated_v1"
    private const val PAGE_SIZE_DEFAULT = 100

    private val io = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "Likho-History-DB").apply { priority = Thread.NORM_PRIORITY - 1 }
    }
    private val mainHandler = Handler(Looper.getMainLooper())
    @Volatile private var databaseHelper: HistoryDatabaseHelper? = null

    fun appendAsync(context: Context, word: String) {
        val normalized = word.trim().replace("\n", " ")
        if (normalized.isBlank() || normalized.length > 120) return
        val appContext = context.applicationContext
        io.execute {
            withDatabase(appContext) { db ->
                migrateLegacyIfNeeded(appContext, db)
                val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                if (!prefs.getBoolean(PREF_HISTORY_ENABLED, true)) return@withDatabase
                insert(db, normalized)
                trimToLimit(db)
            }
        }
    }

    fun loadRecentAsync(context: Context, limit: Int = PAGE_SIZE_DEFAULT, callback: (List<String>) -> Unit) {
        val appContext = context.applicationContext
        io.execute {
            val words = runCatching {
                withDatabaseResult(appContext) { db ->
                    migrateLegacyIfNeeded(appContext, db)
                    if (!appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                            .getBoolean(PREF_HISTORY_ENABLED, true)) emptyList()
                    else queryPage(db, limit.coerceIn(1, HistorySequence.MAX_HISTORY_WORDS), 0).asReversed()
                }
            }.getOrDefault(emptyList())
            mainHandler.post { callback(words) }
        }
    }

    fun loadPageAsync(
        context: Context,
        offset: Int,
        limit: Int = PAGE_SIZE_DEFAULT,
        callback: (HistoryPage) -> Unit
    ) {
        val appContext = context.applicationContext
        val safeOffset = offset.coerceAtLeast(0)
        val safeLimit = limit.coerceIn(1, 500)
        io.execute {
            val page = runCatching {
                withDatabaseResult(appContext) { db ->
                    migrateLegacyIfNeeded(appContext, db)
                    val enabled = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .getBoolean(PREF_HISTORY_ENABLED, true)
                    if (!enabled) HistoryPage(0, emptyList())
                    else {
                        val total = db.rawQuery("SELECT COUNT(*) FROM $TABLE", null).use { cursor ->
                            if (cursor.moveToFirst()) cursor.getInt(0) else 0
                        }
                        HistoryPage(total, queryPage(db, safeLimit, safeOffset))
                    }
                }
            }.getOrElse { HistoryPage(-1, emptyList()) }
            mainHandler.post { callback(page) }
        }
    }

    fun clearAsync(context: Context, callback: (() -> Unit)? = null) {
        val appContext = context.applicationContext
        io.execute {
            withDatabase(appContext) { db ->
                db.delete(TABLE, null, null)
                appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                    .remove(PREF_HISTORY)
                    .putBoolean(PREF_MIGRATED, true)
                    .commit()
            }
            callback?.let { done -> mainHandler.post { done() } }
        }
    }

    private fun queryPage(db: SQLiteDatabase, limit: Int, offset: Int): List<String> {
        val result = ArrayList<String>(limit)
        db.rawQuery(
            "SELECT $COLUMN_WORD FROM $TABLE ORDER BY $COLUMN_ID DESC LIMIT ? OFFSET ?",
            arrayOf(limit.toString(), offset.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) result.add(cursor.getString(0))
        }
        return result
    }

    private fun insert(db: SQLiteDatabase, word: String) {
        val values = ContentValues(2).apply {
            put(COLUMN_WORD, word)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }
        db.insert(TABLE, null, values)
    }

    private fun trimToLimit(db: SQLiteDatabase) {
        db.execSQL(
            "DELETE FROM $TABLE WHERE $COLUMN_ID NOT IN " +
                "(SELECT $COLUMN_ID FROM $TABLE ORDER BY $COLUMN_ID DESC LIMIT ${HistorySequence.MAX_HISTORY_WORDS})"
        )
    }

    private fun migrateLegacyIfNeeded(context: Context, db: SQLiteDatabase) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(PREF_MIGRATED, false)) return

        val legacy = if (prefs.getBoolean(PREF_HISTORY_ENABLED, true)) {
            prefs.getString(PREF_HISTORY, "").orEmpty().lines().filter { it.isNotBlank() }
                .takeLast(HistorySequence.MAX_HISTORY_WORDS)
        } else emptyList()

        if (legacy.isNotEmpty()) {
            db.beginTransaction()
            try {
                legacy.forEach { insert(db, it) }
                trimToLimit(db)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
        prefs.edit().remove(PREF_HISTORY).putBoolean(PREF_MIGRATED, true).commit()
    }

    private fun writableDatabase(context: Context): SQLiteDatabase {
        databaseHelper?.let { return it.writableDatabase }
        return synchronized(this) {
            val helper = databaseHelper ?: HistoryDatabaseHelper(context.applicationContext)
                .also { databaseHelper = it }
            helper.writableDatabase
        }
    }

    private inline fun withDatabase(context: Context, action: (SQLiteDatabase) -> Unit) {
        action(writableDatabase(context))
    }

    private inline fun <T> withDatabaseResult(context: Context, action: (SQLiteDatabase) -> T): T =
        action(writableDatabase(context))

    private class HistoryDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE $TABLE (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_WORD TEXT NOT NULL, " +
                    "$COLUMN_CREATED_AT INTEGER NOT NULL)"
            )
            db.execSQL("CREATE INDEX idx_history_created_at ON $TABLE($COLUMN_ID DESC)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // No destructive migration is currently required. Future schema upgrades go here.
        }
    }
}

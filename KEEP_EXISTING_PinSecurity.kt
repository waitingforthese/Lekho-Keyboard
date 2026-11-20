package com.example.lekho.keyboard

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Stores only a salted, derived PIN hash. The raw PIN is never persisted. */
internal object PinSecurity {
    private const val PREFS_NAME = "lekho_app_security"
    private const val KEY_PIN_SALT = "pin_salt"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_FAILED_ATTEMPTS = "pin_failed_attempts"
    private const val KEY_LOCK_UNTIL = "pin_lock_until"
    private const val PIN_LENGTH = 4
    private const val SALT_LENGTH_BYTES = 16
    private const val DERIVED_KEY_BITS = 256
    private const val PBKDF2_ITERATIONS = 120_000

    fun isPinConfigured(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getString(KEY_PIN_SALT, null).isNullOrBlank() &&
            !prefs.getString(KEY_PIN_HASH, null).isNullOrBlank()
    }

    fun savePin(context: Context, pin: String): Boolean {
        if (!isValidPin(pin)) return false

        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derivePinHash(pin, salt)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.edit()
            .putString(KEY_PIN_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(KEY_PIN_HASH, Base64.encodeToString(hash, Base64.NO_WRAP))
            .commit()
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        if (!isValidPin(pin)) return false
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saltText = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val hashText = prefs.getString(KEY_PIN_HASH, null) ?: return false

        return try {
            val salt = Base64.decode(saltText, Base64.NO_WRAP)
            val savedHash = Base64.decode(hashText, Base64.NO_WRAP)
            val candidateHash = derivePinHash(pin, salt)
            MessageDigest.isEqual(savedHash, candidateHash)
        } catch (_: IllegalArgumentException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    fun lockoutRemainingMs(context: Context): Long {
        val until = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_LOCK_UNTIL, 0L)
        return (until - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    /** Returns the newly applied lockout duration, or zero when no lockout was applied. */
    fun recordFailedAttempt(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val attempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        return if (attempts >= 5) {
            val duration = 30_000L
            prefs.edit()
                .putInt(KEY_FAILED_ATTEMPTS, 0)
                .putLong(KEY_LOCK_UNTIL, System.currentTimeMillis() + duration)
                .apply()
            duration
        } else {
            prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply()
            0L
        }
    }

    fun resetFailedAttempts(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .remove(KEY_FAILED_ATTEMPTS)
            .remove(KEY_LOCK_UNTIL)
            .apply()
    }

    fun isValidPin(pin: String): Boolean =
        pin.length == PIN_LENGTH && pin.all { it in '0'..'9' }

    private fun derivePinHash(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, DERIVED_KEY_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }
}

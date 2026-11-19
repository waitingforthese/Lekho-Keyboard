package com.example.lekho.keyboard

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.icu.text.BreakIterator
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class LekhoInputMethodService : InputMethodService() {
    private enum class Layout { LETTERS, NUMBERS, SYMBOLS }

    private var marathiMode = true
    private var shift = false
    private var layout = Layout.LETTERS
    private var root: LinearLayout? = null
    private var suggestionRow: LinearLayout? = null
    private val history = mutableListOf<String>()
    private val prefs by lazy { getSharedPreferences("lekho_local", Context.MODE_PRIVATE) }

    override fun onCreateInputView(): View {
        loadHistory()
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(5), dp(4), dp(5))
            setBackgroundColor(Color.rgb(225, 232, 239))
        }
        suggestionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        root!!.addView(suggestionRow, LinearLayout.LayoutParams(-1, dp(42)))
        renderKeys()
        refreshSuggestions()
        return root!!
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        loadHistory()
        renderKeys()
        refreshSuggestions()
    }

    private fun renderKeys() {
        val container = root ?: return
        while (container.childCount > 1) container.removeViewAt(1)
        val rows = when (layout) {
            Layout.LETTERS -> listOf(
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "⌫"),
                listOf("?123", "🌐", ",", "space", ".", "↵")
            )
            Layout.NUMBERS -> listOf(
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")", "/"),
                listOf("=\\<", "*", "\"", "'", ":", ";", "!", "?", "⌫"),
                listOf("ABC", "🌐", ",", "space", ".", "↵")
            )
            Layout.SYMBOLS -> listOf(
                listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆"),
                listOf("£", "¢", "€", "¥", "^", "°", "=", "{", "}", "\\"),
                listOf("123", "<", ">", "[", "]", "⌫"),
                listOf("ABC", "🌐", ",", "space", ".", "↵")
            )
        }
        rows.forEach(::addRow)
    }

    private fun addRow(keys: List<String>) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        keys.forEach { key ->
            val special = key in setOf("⇧", "⌫", "?123", "123", "ABC", "🌐", "↵", "=\\<")
            val button = Button(this).apply {
                text = when {
                    key == "space" -> if (marathiMode) "मराठी" else "English"
                    key == "=\\<" -> "\\<="
                    shift && layout == Layout.LETTERS && key.length == 1 && key[0].isLetter() -> key.uppercase(Locale.ROOT)
                    else -> key
                }
                textSize = if (key == "space") 11f else 16f
                isAllCaps = false
                gravity = Gravity.CENTER
                setTextColor(Color.rgb(25, 42, 58))
                typeface = Typeface.DEFAULT
                setPadding(0, 0, 0, 0)
                minWidth = 0
                minHeight = 0
                minimumHeight = 0
                background = GradientDrawable().apply {
                    setColor(if (special || key == "space") Color.rgb(193, 211, 225) else Color.WHITE)
                    cornerRadius = dp(7).toFloat()
                }
                contentDescription = when (key) {
                    "⌫" -> "Backspace"
                    "↵" -> "Enter"
                    "🌐" -> "Switch language"
                    "⇧" -> "Shift"
                    "space" -> if (marathiMode) "Marathi phonetic mode" else "English mode"
                    else -> key
                }
                setOnClickListener { onKey(key) }
            }
            val weight = when (key) {
                "space" -> 4.2f
                "?123", "123", "ABC", "🌐", "⇧", "⌫", "↵", "=\\<" -> 1.35f
                else -> 1f
            }
            row.addView(button, LinearLayout.LayoutParams(0, dp(46), weight).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            })
        }
        root?.addView(row, LinearLayout.LayoutParams(-1, dp(50)))
    }

    private fun onKey(key: String) {
        val connection = currentInputConnection ?: return
        when (key) {
            "⌫" -> deleteOneCharacter()
            "↵" -> performEnter()
            "🌐" -> {
                marathiMode = !marathiMode
                renderKeys()
                refreshSuggestions()
            }
            "⇧" -> { shift = !shift; renderKeys() }
            "?123", "123" -> { layout = Layout.NUMBERS; renderKeys() }
            "=\\<" -> { layout = Layout.SYMBOLS; renderKeys() }
            "ABC" -> { layout = Layout.LETTERS; renderKeys() }
            "space" -> {
                if (isSensitiveField()) {
                    connection.commitText(" ", 1)
                } else {
                    val token = currentToken()
                    val converted = if (marathiMode) WordEngine.transliterate(token) else null
                    if (!token.isNullOrEmpty() && converted != null) {
                        connection.deleteSurroundingText(token.length, 0)
                        connection.commitText("$converted ", 1)
                        remember(converted)
                    } else {
                        connection.commitText(" ", 1)
                        if (token.isNotBlank()) remember(token)
                    }
                }
                shift = false
                refreshSuggestions()
            }
            ",", "." -> {
                connection.commitText(key, 1)
                refreshSuggestions()
            }
            else -> {
                val value = if (shift && layout == Layout.LETTERS) key.uppercase(Locale.ROOT) else key
                connection.commitText(value, 1)
                if (shift) { shift = false; renderKeys() }
                refreshSuggestions()
            }
        }
    }

    private fun deleteOneCharacter() {
        val connection = currentInputConnection ?: return
        val selected = connection.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            connection.commitText("", 1)
            refreshSuggestions()
            return
        }
        val before = connection.getTextBeforeCursor(24, 0)?.toString().orEmpty()
        if (before.isEmpty()) return

        // Delete one Unicode grapheme cluster (e.g. a Devanagari letter plus its matra).
        val iterator = BreakIterator.getCharacterInstance(Locale.getDefault())
        iterator.setText(before)
        val boundary = iterator.preceding(before.length)
        val deleteLength = if (boundary == BreakIterator.DONE) 1 else before.length - boundary
        connection.deleteSurroundingText(deleteLength, 0)
        refreshSuggestions()
    }

    private fun performEnter() {
        val connection = currentInputConnection ?: return
        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
            ?: EditorInfo.IME_ACTION_NONE
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            connection.performEditorAction(action)
        } else {
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun currentToken(): String {
        val before = currentInputConnection?.getTextBeforeCursor(80, 0)?.toString().orEmpty()
        return before.takeLastWhile { it.isLetter() || it == '\'' }
    }

    private fun refreshSuggestions() {
        val row = suggestionRow ?: return
        row.removeAllViews()
        if (isSensitiveField()) return

        val token = currentToken()
        val suggestions = WordEngine.suggestions(token, marathiMode)
        val fallback = if (token.isBlank()) listOf("📋", "↕") else listOf(token, "📋", "↕")
        (suggestions.ifEmpty { fallback }).take(3).forEach { word ->
            val item = TextView(this).apply {
                text = word
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(Color.rgb(20, 48, 66))
                contentDescription = when (word) {
                    "📋" -> "Paste clipboard"
                    "↕" -> "Paste most recent saved word"
                    else -> "Insert $word"
                }
                setOnClickListener {
                    when (word) {
                        "📋" -> pasteClipboard()
                        "↕" -> pasteLastHistory()
                        else -> {
                            val connection = currentInputConnection ?: return@setOnClickListener
                            if (token.isNotEmpty()) connection.deleteSurroundingText(token.length, 0)
                            connection.commitText(word, 1)
                            remember(word)
                            refreshSuggestions()
                        }
                    }
                }
            }
            row.addView(item, LinearLayout.LayoutParams(0, -1, 1f))
        }
    }

    private fun pasteClipboard() {
        if (isSensitiveField()) return
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = clipboard.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString() ?: return
        currentInputConnection?.commitText(text, 1)
        refreshSuggestions()
    }

    private fun pasteLastHistory() {
        if (isSensitiveField() || !prefs.getBoolean("history_enabled", true)) return
        loadHistory()
        val last = history.lastOrNull() ?: return
        currentInputConnection?.commitText(last, 1)
        refreshSuggestions()
    }

    private fun remember(text: String) {
        if (isSensitiveField()) return
        if (!prefs.getBoolean("history_enabled", true) || text.isBlank() || text.length > 100) return
        history.remove(text)
        history.add(text)
        while (history.size > 30) history.removeAt(0)
        prefs.edit().putString("history", history.joinToString("\n")).apply()
    }

    private fun loadHistory() {
        history.clear()
        if (prefs.getBoolean("history_enabled", true)) {
            history.addAll(prefs.getString("history", "").orEmpty().lines().filter { it.isNotBlank() }.takeLast(30))
        }
    }

    private fun isSensitiveField(): Boolean {
        val info = currentInputEditorInfo ?: return false
        val inputType = info.inputType
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        val passwordVariation =
            (inputClass == InputType.TYPE_TEXT_CLASS && variation in setOf(
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            )) ||
            (inputClass == InputType.TYPE_NUMBER_CLASS && variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        val explicitlyPrivate = info.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING != 0
        return passwordVariation || explicitlyPrivate
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

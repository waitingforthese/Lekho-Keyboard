package com.example.lekho.keyboard

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.icu.text.BreakIterator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
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

    // Backspace repeat: tap deletes one grapheme; holding repeats until the finger is lifted.
    private val backspaceHandler = Handler(Looper.getMainLooper())
    private var backspaceHeld = false
    private var backspaceRepeated = false
    private var backspaceCancelled = false
    private var backspaceStartedAt = 0L
    private val repeatBackspace = object : Runnable {
        override fun run() {
            if (!backspaceHeld || backspaceCancelled) return
            backspaceRepeated = true
            deleteOneCharacter(refreshAfterDelete = false)
            val heldFor = SystemClock.uptimeMillis() - backspaceStartedAt
            val repeatDelay = if (heldFor >= 1_000L) 22L else 48L
            backspaceHandler.postDelayed(this, repeatDelay)
        }
    }

    override fun onCreateInputView(): View {
        loadHistory()
        applyNavigationBarStyle()
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(5), dp(4), 0)
            setBackgroundColor(KEYBOARD_BACKGROUND)
            clipToPadding = false
            clipChildren = false
        }
        suggestionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(2), 0, dp(2), 0)
        }
        root!!.addView(suggestionRow, LinearLayout.LayoutParams(-1, dp(44)))
        renderKeys()
        refreshSuggestions()
        return root!!
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        applyNavigationBarStyle()
        loadHistory()
        renderKeys()
        refreshSuggestions()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        stopBackspaceRepeat(refresh = false)
        super.onFinishInputView(finishingInput)
    }

    override fun onDestroy() {
        stopBackspaceRepeat(refresh = false)
        super.onDestroy()
    }

    private fun applyNavigationBarStyle() {
        val imeWindow = window?.window ?: return
        imeWindow.navigationBarColor = KEYBOARD_BACKGROUND
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imeWindow.isNavigationBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decor = imeWindow.decorView
            decor.systemUiVisibility = decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    /** Reserve the device's navigation-bar height so the bottom key row never sits under it. */
    private fun navigationBarSpacerHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val measured = if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else dp(24)
        return measured.coerceIn(dp(18), dp(52))
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

        // Keep system navigation controls in their own bottom strip, below all keys.
        container.addView(View(this).apply {
            setBackgroundColor(KEYBOARD_BACKGROUND)
            isFocusable = false
            isClickable = false
        }, LinearLayout.LayoutParams(-1, navigationBarSpacerHeight()))
    }

    private fun addRow(keys: List<String>) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            clipChildren = false
            clipToPadding = false
        }
        keys.forEach { key ->
            val special = key in setOf("⇧", "⌫", "?123", "123", "ABC", "🌐", "↵", "=\\<")
            val button = Button(this).apply {
                text = when {
                    key == "space" -> if (marathiMode) "मराठी" else "English"
                    key == "=\\<" -> "<="
                    shift && layout == Layout.LETTERS && key.length == 1 && key[0].isLetter() -> key.uppercase(Locale.ROOT)
                    else -> key
                }
                textSize = when {
                    key == "space" -> 12f
                    key.length == 1 && key[0].isLetter() -> 20f
                    else -> 16f
                }
                isAllCaps = false
                gravity = Gravity.CENTER
                setTextColor(KEY_TEXT)
                typeface = Typeface.DEFAULT
                setPadding(0, 0, 0, 0)
                minWidth = 0
                minHeight = 0
                minimumWidth = 0
                minimumHeight = 0
                stateListAnimator = null
                elevation = dp(1).toFloat()
                background = GradientDrawable().apply {
                    val fill = if (special || key == "space") KEY_SPECIAL else Color.WHITE
                    setColor(fill)
                    cornerRadius = dp(8).toFloat()
                    setStroke(dp(1), if (special) KEY_SPECIAL_STROKE else KEY_BORDER)
                }
                contentDescription = when (key) {
                    "⌫" -> "Backspace. Tap to delete one character, hold to keep deleting"
                    "↵" -> "Enter"
                    "🌐" -> "Switch language"
                    "⇧" -> "Shift"
                    "space" -> if (marathiMode) "Space, Marathi phonetic mode" else "Space, English mode"
                    else -> key
                }
                if (key == "⌫") {
                    setOnClickListener { onKey(key) }
                    setOnTouchListener { view, event -> handleBackspaceTouch(view, event) }
                } else {
                    setOnClickListener {
                        it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                        onKey(key)
                    }
                }
            }
            val weight = when (key) {
                "space" -> 4.15f
                "?123", "123", "ABC" -> 1.45f
                "🌐", "⇧", "⌫", "↵", "=\\<" -> 1.35f
                else -> 1f
            }
            row.addView(button, LinearLayout.LayoutParams(0, dp(45), weight).apply {
                setMargins(dp(2), dp(2), dp(2), dp(3))
            })
        }
        root?.addView(row, LinearLayout.LayoutParams(-1, dp(50)))
    }

    private fun handleBackspaceTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                backspaceHeld = true
                backspaceRepeated = false
                backspaceCancelled = false
                backspaceStartedAt = SystemClock.uptimeMillis()
                view.isPressed = true
                backspaceHandler.removeCallbacks(repeatBackspace)
                backspaceHandler.postDelayed(repeatBackspace, 380L)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val inside = event.x >= 0 && event.x < view.width && event.y >= 0 && event.y < view.height
                if (!inside && backspaceHeld) {
                    backspaceCancelled = true
                    backspaceHeld = false
                    backspaceHandler.removeCallbacks(repeatBackspace)
                    view.isPressed = false
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                val shouldClick = backspaceHeld && !backspaceRepeated && !backspaceCancelled
                view.isPressed = false
                backspaceHeld = false
                backspaceHandler.removeCallbacks(repeatBackspace)
                if (shouldClick) view.performClick() else if (backspaceRepeated) refreshSuggestions()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                view.isPressed = false
                backspaceHeld = false
                backspaceCancelled = true
                backspaceHandler.removeCallbacks(repeatBackspace)
                if (backspaceRepeated) refreshSuggestions()
                return true
            }
        }
        return true
    }

    private fun stopBackspaceRepeat(refresh: Boolean) {
        backspaceHeld = false
        backspaceHandler.removeCallbacks(repeatBackspace)
        if (refresh && backspaceRepeated) refreshSuggestions()
        backspaceRepeated = false
        backspaceCancelled = false
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

    private fun deleteOneCharacter(refreshAfterDelete: Boolean = true) {
        val connection = currentInputConnection ?: return
        val selected = connection.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            connection.commitText("", 1)
            if (refreshAfterDelete) refreshSuggestions()
            return
        }
        val before = connection.getTextBeforeCursor(32, 0)?.toString().orEmpty()
        if (before.isEmpty()) return

        // Delete one Unicode grapheme cluster (for example a Devanagari consonant + matra).
        val iterator = BreakIterator.getCharacterInstance(Locale.getDefault())
        iterator.setText(before)
        val boundary = iterator.preceding(before.length)
        val deleteLength = if (boundary == BreakIterator.DONE) 1 else before.length - boundary
        connection.deleteSurroundingText(deleteLength, 0)
        if (refreshAfterDelete) refreshSuggestions()
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
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(KEY_TEXT)
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
            (inputClass == InputType.TYPE_CLASS_TEXT && variation in setOf(
                InputType.TYPE_TEXT_VARIATION_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            )) ||
            (inputClass == InputType.TYPE_CLASS_NUMBER && variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        val explicitlyPrivate = info.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING != 0
        return passwordVariation || explicitlyPrivate
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        private val KEYBOARD_BACKGROUND = Color.rgb(220, 231, 242)
        private val KEY_SPECIAL = Color.rgb(190, 213, 229)
        private val KEY_SPECIAL_STROKE = Color.rgb(174, 199, 217)
        private val KEY_BORDER = Color.rgb(226, 232, 238)
        private val KEY_TEXT = Color.rgb(36, 55, 70)
    }
}

package com.example.lekho.keyboard

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

/**
 * Lekho Keyboard V1.6.3 — expanded English suggestions and pause-aware typing history.
 * Typing history stores committed words and words paused on for a short idle interval; full messages are not stored.
 */
class LekhoInputMethodService : InputMethodService() {
    private enum class Layout { LETTERS, NUMBERS, SYMBOLS }
    private enum class TopStripMode { TOOLBAR, SUGGESTIONS, MORE_TOOLS, EMOJI, HISTORY }

    private var marathiMode = true
    // True while the current Roman word is held as an InputConnection composing span.
    private var composingTokenActive = false
    private var composingSupported = true
    private var shiftEnabled = false
    private var capsLock = false
    private var lastShiftTapAt = 0L
    private var layout = Layout.LETTERS
    private var emojiMode = false
    private var historyMode = false
    private var moreToolsMode = false

    private var root: LinearLayout? = null
    private var suggestionRow: LinearLayout? = null
    private var activeTopStripMode: TopStripMode? = null
    private val suggestionCells = mutableListOf<TextView>()
    private val history = mutableListOf<String>()
    private val prefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    private var englishDictionaryLoaded = false

    // Save an in-progress word to local history when typing pauses, without inserting a space.
    private val typingPauseHandler = Handler(Looper.getMainLooper())
    private var typingTokenActive = false
    // Authoritative local buffer prevents editors with incomplete composing callbacks from
    // truncating the token used for suggestions after the first few keystrokes.
    private var composingTokenBuffer = ""
    private var lastIdleSavedToken: String? = null
    private var lastIdleSavedMarathiMode: Boolean? = null
    private val saveTypingAfterPause = Runnable { saveCurrentTokenAfterPause() }

    // Long-press backspace: single tap deletes one grapheme; hold repeats until release.
    private val backspaceHandler = Handler(Looper.getMainLooper())
    private var backspaceHeld = false
    private var backspaceRepeated = false
    private var backspaceCancelled = false
    private var backspaceStartedAt = 0L
    private val repeatBackspace = object : Runnable {
        override fun run() {
            if (!backspaceHeld || backspaceCancelled) return
            backspaceRepeated = true
            deleteOneGrapheme(refreshAfterDelete = false)
            val heldFor = SystemClock.uptimeMillis() - backspaceStartedAt
            backspaceHandler.postDelayed(this, if (heldFor >= 900L) 28L else 55L)
        }
    }

    override fun onCreateInputView(): View {
        loadEnglishDictionaryIfNeeded()
        loadHistory()
        applyNavigationBarStyle()
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(5), dp(3), dp(5), 0)
            setBackgroundColor(COLOR_PANEL)
            clipChildren = false
            clipToPadding = false
        }
        suggestionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(2), 0, dp(2), 0)
            setBackgroundColor(COLOR_PANEL)
        }
        root!!.addView(suggestionRow, LinearLayout.LayoutParams(-1, dp(46)))
        renderKeys()
        refreshTopStrip()
        return root!!
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        applyNavigationBarStyle()
        loadHistory()
        emojiMode = false
        historyMode = false
        moreToolsMode = false
        activeTopStripMode = null
        suggestionCells.clear()
        shiftEnabled = false
        capsLock = false
        composingTokenActive = false
        composingSupported = true
        typingPauseHandler.removeCallbacks(saveTypingAfterPause)
        typingTokenActive = false
        composingTokenBuffer = ""
        lastIdleSavedToken = null
        lastIdleSavedMarathiMode = null
        renderKeys()
        refreshTopStrip()
    }


    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        // Some editors update their selection/composing range asynchronously. Refreshing
        // here keeps candidates synchronized even when the editor changes the text itself.
        if (root != null) refreshTopStrip()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        stopBackspaceRepeat(refresh = false)
        typingPauseHandler.removeCallbacks(saveTypingAfterPause)
        // Finalize any pending composing word before the editor/IME session is hidden.
        // This also prevents a reopened keyboard from appending to a stale local buffer.
        if (typingTokenActive || composingTokenActive) commitCurrentTokenAndSeparator()
        super.onFinishInputView(finishingInput)
    }

    override fun onDestroy() {
        stopBackspaceRepeat(refresh = false)
        typingPauseHandler.removeCallbacks(saveTypingAfterPause)
        super.onDestroy()
    }

    private fun applyNavigationBarStyle() {
        val imeWindow = window?.window ?: return
        imeWindow.navigationBarColor = COLOR_PANEL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imeWindow.isNavigationBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imeWindow.decorView.systemUiVisibility =
                imeWindow.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    /** Keep all key rows above the device's system navigation controls. */
    private fun navigationBarSpacerHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val measured = if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else dp(24)
        return measured.coerceIn(dp(18), dp(48))
    }

    private fun renderKeys() {
        val container = root ?: return
        while (container.childCount > 1) container.removeViewAt(1)

        if (layout == Layout.LETTERS) {
            addRow(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), height = 51)
            addRow(listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"), height = 54)
            addRow(listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"), height = 54, horizontalInset = 14)
            addRow(listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "⌫"), height = 55)
            addRow(listOf("?123", ",", "🌐", "space", ".", "↵"), height = 56)
        } else if (layout == Layout.NUMBERS) {
            addRow(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), height = 55)
            addRow(listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")", "/"), height = 55)
            addRow(listOf("=\\<", "*", "\"", "'", ":", ";", "!", "?", "⌫"), height = 55)
            addRow(listOf("ABC", "😊", "🌐", "space", ".", "↵"), height = 56)
        } else {
            addRow(listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆"), height = 55)
            addRow(listOf("£", "¢", "€", "¥", "^", "°", "=", "{", "}", "\\"), height = 55)
            addRow(listOf("123", "<", ">", "[", "]", "⌫"), height = 55, horizontalInset = 14)
            addRow(listOf("ABC", "😊", "🌐", "space", ".", "↵"), height = 56)
        }

        // The IME window paints the navigation strip in the same tint. The spacer prevents
        // the final key row from colliding with three-button or gesture navigation areas.
        container.addView(View(this).apply {
            setBackgroundColor(COLOR_PANEL)
            isFocusable = false
            isClickable = false
        }, LinearLayout.LayoutParams(-1, navigationBarSpacerHeight()))
    }

    private fun addRow(keys: List<String>, height: Int, horizontalInset: Int = 0) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            clipChildren = false
            clipToPadding = false
            if (horizontalInset > 0) setPadding(dp(horizontalInset), 0, dp(horizontalInset), 0)
        }
        keys.forEach { key ->
            val isModifier = key in setOf("⇧", "⌫", "?123", "123", "ABC", "🌐", "↵", "=\\<", ",")
            val isShiftActive = key == "⇧" && (shiftEnabled || capsLock)
            val button = Button(this).apply {
                text = when {
                    key == "space" -> if (marathiMode) "मराठी" else "English"
                    key == "=\\<" -> "<="
                    key == "⇧" && capsLock -> "⬆"
                    key == "⇧" -> "⇧"
                    key.length == 1 && key[0].isLetter() && (shiftEnabled || capsLock) -> key.uppercase(Locale.ROOT)
                    else -> key
                }
                textSize = when {
                    key == "space" -> 15f
                    key.length == 1 && key[0].isLetter() -> 20f
                    key == "↵" || key == "⌫" || key == "⇧" -> 20f
                    else -> 16f
                }
                isAllCaps = false
                gravity = Gravity.CENTER
                setTextColor(when { key == "🌐" -> COLOR_GLOBE; isModifier || key == "space" -> COLOR_MODIFIER_TEXT; else -> COLOR_KEY_TEXT })
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                includeFontPadding = false
                setPadding(0, 0, 0, 0)
                minWidth = 0
                minHeight = 0
                minimumWidth = 0
                minimumHeight = 0
                stateListAnimator = null
                elevation = dp(if (isModifier) 1 else 2).toFloat()
                background = roundedKeyBackground(
                    fill = when {
                        isShiftActive -> COLOR_ACTIVE_MODIFIER
                        isModifier || key == "space" || key == "😊" -> COLOR_MODIFIER
                        else -> Color.WHITE
                    },
                    stroke = if (isModifier || key == "space" || key == "😊") COLOR_MODIFIER_BORDER else COLOR_KEY_BORDER,
                    radius = if (key == "space") 10 else 9
                )
                contentDescription = when (key) {
                    "⌫" -> "Backspace. Tap to delete one character, hold to keep deleting"
                    "↵" -> "Enter or editor action"
                    "🌐" -> "Switch Marathi and English"
                    "⇧" -> if (capsLock) "Caps lock on" else "Shift"
                    "space" -> if (marathiMode) "Space, Marathi mode" else "Space, English mode"
                    "😊" -> "Open emoji shortcuts"
                    "," -> "Comma"
                    else -> key
                }
                if (key == "⌫") {
                    setOnClickListener { onKey(key) }
                    setOnTouchListener { view, event -> handleBackspaceTouch(view, event) }
                } else {
                    setOnClickListener {
                        it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        onKey(key)
                    }
                }
            }
            val weight = when (key) {
                "space" -> 3.7f
                "?123", "123", "ABC", "↵", "⇧", "⌫" -> 1.1f
                "🌐" -> 1.05f
                ",", "." -> 1.0f
                "=\\<" -> 1.15f
                else -> 1f
            }
            row.addView(button, LinearLayout.LayoutParams(0, dp(height), weight).apply {
                setMargins(dp(2), dp(2), dp(2), dp(3))
            })
        }
        root?.addView(row, LinearLayout.LayoutParams(-1, dp(height + 5)))
    }

    private fun roundedKeyBackground(fill: Int, stroke: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), stroke)
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
                if (shouldClick) view.performClick() else if (backspaceRepeated) refreshTopStrip()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                view.isPressed = false
                backspaceHeld = false
                backspaceCancelled = true
                backspaceHandler.removeCallbacks(repeatBackspace)
                if (backspaceRepeated) refreshTopStrip()
                return true
            }
        }
        return true
    }

    private fun stopBackspaceRepeat(refresh: Boolean) {
        backspaceHeld = false
        backspaceHandler.removeCallbacks(repeatBackspace)
        if (refresh && backspaceRepeated) refreshTopStrip()
        backspaceRepeated = false
        backspaceCancelled = false
    }

    private fun onShiftPressed() {
        val now = SystemClock.uptimeMillis()
        when {
            capsLock -> {
                capsLock = false
                shiftEnabled = false
            }
            shiftEnabled && now - lastShiftTapAt <= 450L -> {
                capsLock = true
                shiftEnabled = true
            }
            shiftEnabled -> shiftEnabled = false
            else -> shiftEnabled = true
        }
        lastShiftTapAt = now
        renderKeys()
    }

    private fun onKey(key: String) {
        val connection = currentInputConnection ?: return
        when (key) {
            "⌫" -> deleteOneGrapheme()
            "↵" -> performEnter()
            "🌐" -> {
                if (typingTokenActive || composingTokenActive) commitCurrentTokenAndSeparator()
                marathiMode = !marathiMode
                emojiMode = false
                historyMode = false
                moreToolsMode = false
                renderKeys()
                refreshTopStrip()
            }
            "⇧" -> onShiftPressed()
            "?123", "123" -> { layout = Layout.NUMBERS; renderKeys() }
            "=\\<" -> { layout = Layout.SYMBOLS; renderKeys() }
            "ABC" -> { layout = Layout.LETTERS; renderKeys() }
            "😊" -> {
                emojiMode = !emojiMode
                historyMode = false
                moreToolsMode = false
                refreshTopStrip()
            }
            "space" -> {
                commitCurrentTokenAndSeparator(" ")
                val shiftWasReset = shiftEnabled && !capsLock
                if (shiftWasReset) shiftEnabled = false
                if (shiftWasReset) renderKeys()
                refreshTopStrip()
            }
            ",", "." -> {
                commitCurrentTokenAndSeparator(key)
                refreshTopStrip()
            }
            else -> {
                val value = if ((shiftEnabled || capsLock) && layout == Layout.LETTERS) key.uppercase(Locale.ROOT) else key
                val isAlphabeticKey = layout == Layout.LETTERS && key.length == 1 && key[0].isLetter()
                if (isAlphabeticKey && !isSensitiveField()) {
                    // Keep the complete current token active so suggestions can update on
                    // every keypress. Editors that reject composing spans still get a
                    // plain-text fallback and the same idle-history behavior.
                    val previousToken = if (typingTokenActive) composingTokenBuffer else currentTokenFromEditor()
                    val nextToken = previousToken + value
                    composingTokenBuffer = nextToken
                    typingTokenActive = true
                    if (composingSupported) {
                        composingTokenActive = connection.setComposingText(nextToken, 1)
                        if (!composingTokenActive) {
                            connection.commitText(nextToken, 1)
                            composingSupported = false
                        }
                    } else {
                        connection.commitText(value, 1)
                    }
                    scheduleTypingPauseSave()
                } else {
                    if (composingTokenActive || typingTokenActive) {
                        // End the current word before inserting a number or symbol key,
                        // including in editors that do not support composing spans.
                        commitCurrentTokenAndSeparator()
                    }
                    connection.commitText(value, 1)
                }
                if (shiftEnabled && !capsLock && layout == Layout.LETTERS) {
                    shiftEnabled = false
                    renderKeys()
                }
                refreshTopStrip()
            }
        }
    }

    private fun deleteOneGrapheme(refreshAfterDelete: Boolean = true) {
        val connection = currentInputConnection ?: return
        val selected = connection.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            connection.commitText("", 1)
            composingTokenActive = false
            composingTokenBuffer = currentTokenFromEditor()
            updateTypingPauseAfterEdit()
            if (refreshAfterDelete) refreshTopStrip()
            return
        }
        if (typingTokenActive || composingTokenActive) {
            val token = currentToken()
            if (token.isNotEmpty()) {
                val iterator = BreakIterator.getCharacterInstance(Locale.getDefault())
                iterator.setText(token)
                val boundary = iterator.preceding(token.length)
                val keepLength = if (boundary == BreakIterator.DONE) 0 else boundary
                val shortened = token.substring(0, keepLength)
                val removedLength = token.length - shortened.length
                if (composingTokenActive) {
                    if (shortened.isEmpty()) {
                        connection.commitText("", 1)
                        composingTokenActive = false
                    } else {
                        val updated = connection.setComposingText(shortened, 1)
                        composingTokenActive = updated
                        if (!updated) {
                            connection.deleteSurroundingText(removedLength, 0)
                            composingSupported = false
                        }
                    }
                } else if (removedLength > 0) {
                    connection.deleteSurroundingText(removedLength, 0)
                }
                composingTokenBuffer = shortened
                if (shortened.isEmpty()) {
                    typingTokenActive = false
                    typingPauseHandler.removeCallbacks(saveTypingAfterPause)
                    lastIdleSavedToken = null
                    lastIdleSavedMarathiMode = null
                } else {
                    typingTokenActive = true
                    scheduleTypingPauseSave()
                }
                if (refreshAfterDelete) refreshTopStrip()
                return
            }
            composingTokenActive = false
            typingTokenActive = false
            composingTokenBuffer = ""
        }
        val before = connection.getTextBeforeCursor(48, 0)?.toString().orEmpty()
        if (before.isEmpty()) return
        val iterator = BreakIterator.getCharacterInstance(Locale.getDefault())
        iterator.setText(before)
        val boundary = iterator.preceding(before.length)
        val deleteLength = if (boundary == BreakIterator.DONE) 1 else before.length - boundary
        connection.deleteSurroundingText(deleteLength, 0)
        updateTypingPauseAfterEdit()
        if (refreshAfterDelete) refreshTopStrip()
    }

    /**
     * Handles both IME actions (Send/Search/Done) and a real newline for multiline editors.
     * Some apps return false from performEditorAction; in that case send Enter key events,
     * and commit a newline only when the editor did not accept the key events.
     */
    private fun performEnter() {
        val connection = currentInputConnection ?: return
        commitCurrentTokenAndSeparator()
        val info = currentInputEditorInfo
        val inputType = info?.inputType ?: InputType.TYPE_CLASS_TEXT
        val imeOptions = info?.imeOptions ?: EditorInfo.IME_ACTION_NONE
        val action = imeOptions and EditorInfo.IME_MASK_ACTION
        val hasEditorAction = action != EditorInfo.IME_ACTION_NONE &&
            action != EditorInfo.IME_ACTION_UNSPECIFIED
        val isMultiline = (inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0
        val editorForcesNewLine = (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0

        // Messaging apps often use a multiline field but still expose IME_ACTION_SEND.
        // Honor an explicit Send/Search/Done action before choosing a newline.
        val shouldInsertNewLine = editorForcesNewLine || (isMultiline && !hasEditorAction)
        if (shouldInsertNewLine) {
            connection.commitText("\n", 1)
        } else {
            val handledByAction = hasEditorAction && connection.performEditorAction(action)
            if (!handledByAction) {
                val downAccepted = connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                val upAccepted = connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                if (!downAccepted && !upAccepted) connection.commitText("\n", 1)
            }
        }
        val shiftWasReset = shiftEnabled && !capsLock
        if (shiftWasReset) shiftEnabled = false
        if (shiftWasReset) renderKeys()
        refreshTopStrip()
    }

    private fun currentToken(): String {
        if (typingTokenActive && composingTokenBuffer.isNotEmpty()) return composingTokenBuffer
        return currentTokenFromEditor()
    }

    private fun currentTokenFromEditor(): String {
        val before = currentInputConnection?.getTextBeforeCursor(96, 0)?.toString().orEmpty()
        return before.takeLastWhile { it.isLetter() || it == '\'' }
    }

    /**
     * Finalizes the Roman token as Marathi before a word boundary or editor action.
     * This also covers punctuation and Enter, not only the Space key.
     */
    private fun commitCurrentTokenAndSeparator(separator: String? = null) {
        val connection = currentInputConnection ?: return
        typingPauseHandler.removeCallbacks(saveTypingAfterPause)
        if (isSensitiveField()) {
            separator?.let { connection.commitText(it, 1) }
            typingTokenActive = false
            composingTokenActive = false
            composingTokenBuffer = ""
            lastIdleSavedToken = null
            lastIdleSavedMarathiMode = null
            return
        }

        val token = currentToken()
        if (token.isNotEmpty()) {
            val completedWord = if (marathiMode) WordEngine.transliterate(token) ?: token else token
            if (composingTokenActive) {
                // commitText replaces the active composing span and finalizes it.
                connection.commitText(completedWord + (separator ?: ""), 1)
            } else {
                connection.deleteSurroundingText(token.length, 0)
                connection.commitText(completedWord, 1)
                separator?.let { connection.commitText(it, 1) }
            }
            val alreadySavedDuringPause = lastIdleSavedToken == token && lastIdleSavedMarathiMode == marathiMode
            if (!alreadySavedDuringPause) remember(completedWord)
        } else {
            separator?.let { connection.commitText(it, 1) }
        }
        composingTokenActive = false
        typingTokenActive = false
        composingTokenBuffer = ""
        lastIdleSavedToken = null
        lastIdleSavedMarathiMode = null
    }

    private fun scheduleTypingPauseSave() {
        typingPauseHandler.removeCallbacks(saveTypingAfterPause)
        if (!typingTokenActive || isSensitiveField()) return
        typingPauseHandler.postDelayed(saveTypingAfterPause, TYPING_PAUSE_SAVE_DELAY_MS)
    }

    private fun updateTypingPauseAfterEdit() {
        val hasToken = currentTokenFromEditor().isNotBlank() && !isSensitiveField()
        typingTokenActive = hasToken
        composingTokenBuffer = if (hasToken) currentTokenFromEditor() else ""
        if (hasToken) {
            scheduleTypingPauseSave()
        } else {
            typingPauseHandler.removeCallbacks(saveTypingAfterPause)
            lastIdleSavedToken = null
            lastIdleSavedMarathiMode = null
        }
    }

    private fun saveCurrentTokenAfterPause() {
        if (!typingTokenActive || isSensitiveField()) return
        val token = currentToken()
        if (token.isBlank()) return
        if (lastIdleSavedToken == token && lastIdleSavedMarathiMode == marathiMode) return
        val word = if (marathiMode) WordEngine.transliterate(token) ?: token else token
        remember(word)
        lastIdleSavedToken = token
        lastIdleSavedMarathiMode = marathiMode
    }

    private fun loadEnglishDictionaryIfNeeded() {
        if (englishDictionaryLoaded) return
        val words = runCatching {
            assets.open("english_words.txt").bufferedReader().useLines { sequence -> sequence.toList() }
        }.getOrDefault(emptyList())
        WordEngine.addEnglishDictionaryWords(words)
        englishDictionaryLoaded = true
    }

    private fun refreshTopStrip() {
        val row = suggestionRow ?: return
        if (isSensitiveField()) {
            if (row.childCount > 0) row.removeAllViews()
            suggestionCells.clear()
            activeTopStripMode = null
            return
        }

        // Read the editor token once per refresh. Normal typing updates ten reusable
        // suggestion TextViews inside a horizontal scroller; toolbar/history/emoji views are rebuilt only
        // when the user changes top-strip mode.
        val token = currentToken()
        val mode = when {
            moreToolsMode -> TopStripMode.MORE_TOOLS
            emojiMode -> TopStripMode.EMOJI
            historyMode -> TopStripMode.HISTORY
            token.isNotBlank() -> TopStripMode.SUGGESTIONS
            else -> TopStripMode.TOOLBAR
        }

        if (mode != activeTopStripMode) {
            row.removeAllViews()
            suggestionCells.clear()
            activeTopStripMode = mode
            when (mode) {
                TopStripMode.SUGGESTIONS -> createSuggestionCells(row)
                TopStripMode.TOOLBAR -> showToolbar(row)
                TopStripMode.MORE_TOOLS -> showMoreTools(row)
                TopStripMode.EMOJI -> showChoices(row, EMOJI_SHORTCUTS, "Emoji") { chosen ->
                    currentInputConnection?.commitText(chosen, 1)
                    refreshTopStrip()
                }
                TopStripMode.HISTORY -> showChoices(row, history.asReversed().take(6), "Recent words") { chosen ->
                    currentInputConnection?.commitText(chosen, 1)
                    refreshTopStrip()
                }
            }
        }

        if (mode == TopStripMode.SUGGESTIONS) updateSuggestionCells(token)
    }

    private fun createSuggestionCells(row: LinearLayout) {
        val scroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isFillViewport = true
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        val choices = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        scroll.addView(choices, android.widget.FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ))
        row.addView(scroll, LinearLayout.LayoutParams(-1, -1))

        repeat(10) {
            val cell = TextView(this).apply {
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(COLOR_TOOL_TEXT)
                isClickable = true
                isFocusable = true
                isSingleLine = true
                ellipsize = android.text.TextUtils.TruncateAt.END
                minWidth = dp(58)
                setPadding(dp(8), 0, dp(8), 0)
                background = roundedKeyBackground(COLOR_PANEL, COLOR_PANEL, 10)
                setOnClickListener { view ->
                    val word = view.tag as? String ?: return@setOnClickListener
                    val connection = currentInputConnection ?: return@setOnClickListener
                    typingPauseHandler.removeCallbacks(saveTypingAfterPause)
                    val tokenNow = currentToken()
                    val pauseSavedWord = if (marathiMode) WordEngine.transliterate(tokenNow) ?: tokenNow else tokenNow
                    val alreadySavedDuringPause = lastIdleSavedToken == tokenNow &&
                        lastIdleSavedMarathiMode == marathiMode && pauseSavedWord == word
                    if (composingTokenActive) {
                        // A selected candidate is a completed word: insert its trailing
                        // space automatically so the user can immediately type the next word.
                        connection.commitText("$word ", 1)
                    } else {
                        if (tokenNow.isNotEmpty()) connection.deleteSurroundingText(tokenNow.length, 0)
                        connection.commitText("$word ", 1)
                    }
                    composingTokenActive = false
                    typingTokenActive = false
                    composingTokenBuffer = ""
                    lastIdleSavedToken = null
                    lastIdleSavedMarathiMode = null
                    if (!alreadySavedDuringPause) remember(word)
                    moreToolsMode = false
                    refreshTopStrip()
                }
            }
            suggestionCells += cell
            choices.addView(cell, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(dp(1), dp(1), dp(1), dp(1))
            })
        }
    }

    private fun updateSuggestionCells(token: String) {
        val suggestions = WordEngine.suggestions(token, marathiMode).take(10)
        val visible = if (suggestions.isEmpty()) listOf(token) else suggestions
        suggestionCells.forEachIndexed { index, cell ->
            val word = visible.getOrNull(index)
            if (word == null) {
                cell.visibility = View.GONE
                cell.tag = null
            } else {
                cell.visibility = View.VISIBLE
                cell.text = word
                cell.tag = word
                cell.contentDescription = "Insert $word"
            }
        }
    }

    private fun showToolbar(row: LinearLayout) {
        addTopItem(row, "▦", "More tools", 1f) {
            moreToolsMode = true
            historyMode = false
            emojiMode = false
            refreshTopStrip()
        }
        addTopItem(row, "⚙", "Keyboard settings", 1f) {
            try {
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: Exception) { }
        }
        addTopItem(row, "文A", "Switch Marathi and English", 1f) {
            marathiMode = !marathiMode
            moreToolsMode = false
            renderKeys()
            refreshTopStrip()
        }
        addTopItem(row, "▤", "Paste clipboard", 1f) { pasteClipboard() }
        addTopItem(row, "◷", "Typing history", 1f) {
            loadHistory()
            historyMode = true
            emojiMode = false
            refreshTopStrip()
        }
    }


    private fun showMoreTools(row: LinearLayout) {
        addTopItem(row, "‹", "Back to keyboard", 0.8f) {
            moreToolsMode = false
            refreshTopStrip()
        }
        addTopItem(row, "◷", "Typing history", 1f) {
            loadHistory()
            historyMode = true
            moreToolsMode = false
            refreshTopStrip()
        }
        addTopItem(row, "😊", "Emoji shortcuts", 1f) {
            emojiMode = true
            moreToolsMode = false
            refreshTopStrip()
        }
        addTopItem(row, "文A", "Switch language", 1f) {
            marathiMode = !marathiMode
            moreToolsMode = false
            renderKeys()
            refreshTopStrip()
        }
        addTopItem(row, "⚙", "Keyboard settings", 1f) {
            moreToolsMode = false
            try {
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: Exception) { }
            refreshTopStrip()
        }
    }

    private fun showChoices(row: LinearLayout, choices: List<String>, label: String, onChoose: (String) -> Unit) {
        if (choices.isEmpty()) {
            addTopItem(row, "‹", "Back to keyboard", 0.8f) {
                emojiMode = false
                historyMode = false
                refreshTopStrip()
            }
            addTopItem(row, label, label, 3.5f) { }
            return
        }
        addTopItem(row, "‹", "Back to keyboard", 0.8f) {
            emojiMode = false
            historyMode = false
            refreshTopStrip()
        }
        choices.take(5).forEach { choice -> addTopItem(row, choice, choice, 1f) { onChoose(choice) } }
    }

    private fun addTopItem(row: LinearLayout, text: String, description: String, weight: Float, onClick: () -> Unit) {
        val item = TextView(this).apply {
            this.text = text
            textSize = if (text.length > 2) 14f else 21f
            gravity = Gravity.CENTER
            setTextColor(COLOR_TOOL_TEXT)
            contentDescription = description
            isClickable = true
            isFocusable = true
            setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }
            background = roundedKeyBackground(COLOR_PANEL, COLOR_PANEL, 10)
        }
        row.addView(item, LinearLayout.LayoutParams(0, -1, weight).apply {
            setMargins(dp(1), dp(1), dp(1), dp(1))
        })
    }

    private fun pasteClipboard() {
        if (isSensitiveField()) return
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = clipboard.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString() ?: return
        currentInputConnection?.commitText(text, 1)
        refreshTopStrip()
    }

    private fun remember(text: String) {
        if (isSensitiveField()) return
        val normalized = text.trim().replace('\n', ' ')
        if (!prefs.getBoolean(KEY_HISTORY_ENABLED, true) || normalized.isBlank() || normalized.length > 120) return
        // Preserve every committed occurrence in order, including repeated words.
        val updatedHistory = HistorySequence.append(history, normalized, MAX_HISTORY_ITEMS)
        history.clear()
        history.addAll(updatedHistory)
        prefs.edit().putString(KEY_HISTORY, history.joinToString("\n")).apply()
    }

    private fun loadHistory() {
        history.clear()
        if (prefs.getBoolean(KEY_HISTORY_ENABLED, true)) {
            history.addAll(
                prefs.getString(KEY_HISTORY, "").orEmpty()
                    .lines().filter { it.isNotBlank() }.takeLast(MAX_HISTORY_ITEMS)
            )
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

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val PREFS_NAME = "lekho_local"
        private const val KEY_HISTORY = "history"
        private const val KEY_HISTORY_ENABLED = "history_enabled"
        private const val MAX_HISTORY_ITEMS = 100
        private const val TYPING_PAUSE_SAVE_DELAY_MS = 900L
        private val EMOJI_SHORTCUTS = listOf("😀", "😂", "😊", "😍", "🙏", "👍", "❤️", "🎉", "😎", "🥰")

        private val COLOR_PANEL = Color.rgb(241, 240, 247)
        private val COLOR_MODIFIER = Color.rgb(226, 230, 252)
        private val COLOR_ACTIVE_MODIFIER = Color.rgb(207, 216, 253)
        private val COLOR_MODIFIER_BORDER = Color.rgb(224, 227, 242)
        private val COLOR_KEY_BORDER = Color.rgb(226, 226, 233)
        private val COLOR_KEY_TEXT = Color.rgb(32, 33, 36)
        private val COLOR_MODIFIER_TEXT = Color.rgb(43, 48, 65)
        private val COLOR_TOOL_TEXT = Color.rgb(35, 38, 47)
        private val COLOR_GLOBE = Color.rgb(0, 188, 212)
    }
}

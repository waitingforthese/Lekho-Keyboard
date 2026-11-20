package com.example.lekho.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("lekho_local", Context.MODE_PRIVATE) }
    private lateinit var historyContainer: LinearLayout
    private var isUnlocked = false
    private var lockOnReturn = false
    private var mainContentVisible = false
    private var historyOffset = 0
    private var historyLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PinSecurity.isPinConfigured(this)) {
            showUnlockScreen()
        } else {
            showInitialPinSetup()
        }
    }

    override fun onStart() {
        super.onStart()
        if (lockOnReturn && PinSecurity.isPinConfigured(this)) {
            lockOnReturn = false
            isUnlocked = false
            showUnlockScreen()
        }
    }

    override fun onStop() {
        if (isUnlocked) lockOnReturn = true
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (mainContentVisible && isUnlocked && ::historyContainer.isInitialized) {
            refreshHistory()
        }
    }

    private fun showUnlockScreen() {
        isUnlocked = false
        showPinEntryScreen(
            title = "लिखो लॉक आहे",
            subtitle = "अॅप उघडण्यासाठी तुमचा 4 अंकी PIN टाका",
            actionLabel = "Unlock"
        ) { enteredPin ->
            val remaining = PinSecurity.lockoutRemainingMs(this)
            if (remaining > 0L) {
                "खूप चुकीचे प्रयत्न झाले. ${((remaining + 999L) / 1000L)} सेकंदांनी पुन्हा प्रयत्न करा."
            } else if (PinSecurity.verifyPin(this, enteredPin)) {
                PinSecurity.resetFailedAttempts(this)
                isUnlocked = true
                lockOnReturn = false
                showMainScreen()
                null
            } else {
                val cooldown = PinSecurity.recordFailedAttempt(this)
                if (cooldown > 0L) "5 चुकीचे प्रयत्न झाले. 30 सेकंद थांबा." else "चुकीचा PIN. पुन्हा प्रयत्न करा."
            }
        }
    }

    private fun showInitialPinSetup() {
        showNewPinFlow {
            isUnlocked = true
            lockOnReturn = false
            Toast.makeText(this, "App PIN सेट झाला.", Toast.LENGTH_SHORT).show()
            showMainScreen()
        }
    }

    private fun showNewPinFlow(onSaved: () -> Unit) {
        var firstPin: String? = null
        showPinEntryScreen(
            title = "Create App PIN",
            subtitle = "तुमचा नवीन 4 अंकी PIN तयार करा",
            actionLabel = "Continue"
        ) { enteredPin ->
            firstPin = enteredPin
            showPinEntryScreen(
                title = "Confirm App PIN",
                subtitle = "तोच 4 अंकी PIN पुन्हा टाका",
                actionLabel = "Save PIN"
            ) { confirmation ->
                if (confirmation != firstPin) {
                    "दोन्ही PIN जुळत नाहीत. पुन्हा प्रयत्न करा."
                } else if (!PinSecurity.savePin(this, confirmation)) {
                    "PIN सेव्ह झाला नाही. पुन्हा प्रयत्न करा."
                } else {
                    onSaved()
                    null
                }
            }
            null
        }
    }

    /** Draws a numeric keypad in-app so PIN entry does not depend on the active IME. */
    private fun showPinEntryScreen(
        title: String,
        subtitle: String,
        actionLabel: String,
        onSubmit: (String) -> String?
    ) {
        mainContentVisible = false
        val pinBuffer = StringBuilder()
        val screen = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(28), dp(30), dp(28), dp(24))
            setBackgroundColor(BG)
        }
        screen.addView(brandLogo(112))

        screen.addView(TextView(this).apply {
            text = "लिखो"
            textSize = 27f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setTextColor(NAVY)
            gravity = Gravity.CENTER
        }, fullWidth())

        screen.addView(TextView(this).apply {
            text = title
            textSize = 21f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setTextColor(NAVY)
            gravity = Gravity.CENTER
            setPadding(0, dp(30), 0, dp(7))
        }, fullWidth())

        screen.addView(TextView(this).apply {
            text = subtitle
            textSize = 14f
            setTextColor(MUTED)
            gravity = Gravity.CENTER
        }, fullWidth())

        val pinDisplay = TextView(this).apply {
            textSize = 27f
            letterSpacing = 0.35f
            gravity = Gravity.CENTER
            setTextColor(NAVY)
            setPadding(0, dp(24), 0, dp(16))
        }
        screen.addView(pinDisplay, fullWidth())

        val errorText = TextView(this).apply {
            textSize = 13f
            setTextColor(ERROR)
            gravity = Gravity.CENTER
            minHeight = dp(32)
        }
        screen.addView(errorText, fullWidth())

        fun refreshPinDisplay() {
            pinDisplay.text = "● ".repeat(pinBuffer.length) + "○ ".repeat(4 - pinBuffer.length)
            errorText.text = ""
        }
        refreshPinDisplay()

        val keypad = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("⌫", "0", "C")
        )
        keypad.forEach { rowKeys ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            rowKeys.forEach { key ->
                val button = Button(this).apply {
                    text = key
                    isAllCaps = false
                    textSize = if (key.length == 1 && key[0].isDigit()) 22f else 17f
                    setTextColor(TEXT)
                    background = roundedButton(Color.WHITE, KEY_BORDER)
                    stateListAnimator = null
                    setOnClickListener {
                        when (key) {
                            "⌫" -> if (pinBuffer.isNotEmpty()) pinBuffer.deleteCharAt(pinBuffer.length - 1)
                            "C" -> pinBuffer.clear()
                            else -> if (pinBuffer.length < 4) pinBuffer.append(key)
                        }
                        refreshPinDisplay()
                    }
                }
                row.addView(button, LinearLayout.LayoutParams(0, dp(58), 1f).apply {
                    setMargins(dp(5), dp(4), dp(5), dp(4))
                })
            }
            screen.addView(row, LinearLayout.LayoutParams(-1, dp(66)))
        }

        screen.addView(actionButton(actionLabel) {
            if (pinBuffer.length != 4) {
                errorText.text = "कृपया पूर्ण 4 अंकी PIN टाका."
            } else {
                val error = onSubmit(pinBuffer.toString())
                if (!error.isNullOrBlank()) errorText.text = error
            }
        }, LinearLayout.LayoutParams(-1, dp(52)).apply {
            topMargin = dp(18)
        })

        setContentView(screen)
    }

    private fun showMainScreen() {
        mainContentVisible = true
        val scroll = ScrollView(this).apply { setBackgroundColor(BG) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(30), dp(22), dp(24))
            setBackgroundColor(BG)
        }
        root.addView(brandLogo(96))
        root.addView(TextView(this).apply {
            text = "लिखो"
            textSize = 29f
            setTextColor(NAVY)
        })
        root.addView(TextView(this).apply {
            text = "Version 1.6.8 · Marathi + English Keyboard"
            textSize = 14f
            setTextColor(MUTED)
            setPadding(0, dp(5), 0, dp(20))
        })
        root.addView(TextView(this).apply {
            text = "Keyboard setup"
            textSize = 19f
            setTextColor(NAVY)
            setPadding(0, 0, 0, dp(8))
        })
        root.addView(actionButton("1. Enable लिखो") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }, match())
        root.addView(actionButton("2. Choose लिखो") {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showInputMethodPicker()
        }, match())

        root.addView(TextView(this).apply {
            text = "App Lock"
            textSize = 19f
            setTextColor(NAVY)
            setPadding(0, dp(22), 0, dp(5))
        })
        root.addView(TextView(this).apply {
            text = "PIN protection is ON. A 4-digit PIN is required whenever you reopen this app. The keyboard remains usable in other apps without asking for this app PIN."
            textSize = 13f
            setTextColor(MUTED)
            setPadding(0, 0, 0, dp(8))
        })
        root.addView(actionButton("Change App PIN") {
            showPinEntryScreen(
                title = "Verify Current PIN",
                subtitle = "PIN बदलण्यापूर्वी सध्याचा PIN टाका",
                actionLabel = "Verify"
            ) { currentPin ->
                if (PinSecurity.lockoutRemainingMs(this) > 0L) {
                    "PIN बदलण्यापूर्वी थोडा वेळ थांबा."
                } else if (PinSecurity.verifyPin(this, currentPin)) {
                    PinSecurity.resetFailedAttempts(this)
                    showNewPinFlow {
                        Toast.makeText(this, "App PIN बदलला.", Toast.LENGTH_SHORT).show()
                        isUnlocked = true
                        lockOnReturn = false
                        showMainScreen()
                    }
                    null
                } else {
                    PinSecurity.recordFailedAttempt(this)
                    "सध्याचा PIN चुकीचा आहे."
                }
            }
        }, match())
        root.addView(TextView(this).apply {
            text = "PIN विसरल्यास Android Settings मधून अॅपचा data clear केल्याने PIN reset होईल; मात्र या अॅपमधील स्थानिक Typing History आणि सेटिंग्जही मिटतील."
            textSize = 12f
            setTextColor(MUTED)
            setPadding(0, 0, 0, dp(8))
        })

        root.addView(TextView(this).apply {
            text = "Privacy and typing history"
            textSize = 19f
            setTextColor(NAVY)
            setPadding(0, dp(22), 0, dp(5))
        })
        root.addView(Switch(this).apply {
            text = "Save typed words on this device"
            textSize = 15f
            setTextColor(TEXT)
            isChecked = prefs.getBoolean("history_enabled", true)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("history_enabled", checked).apply()
                if (!checked) {
                    HistoryStore.clearAsync(this) { refreshHistory() }
                } else {
                    refreshHistory()
                }
            }
        }, match())
        root.addView(actionButton("Clear saved typing history") {
            HistoryStore.clearAsync(this) { refreshHistory() }
        }, match())
        root.addView(TextView(this).apply {
            text = "Recent typed words"
            textSize = 18f
            setTextColor(NAVY)
            setPadding(0, dp(20), 0, dp(8))
        })
        historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = roundedButton(Color.WHITE, Color.TRANSPARENT)
        }
        root.addView(historyContainer, match())
        root.addView(TextView(this).apply {
            text = "Up to 20,000 words are saved locally. Older entries are removed automatically when the limit is reached. Password fields and editors that disable personalized learning are excluded. History can be disabled or cleared here."
            textSize = 13f
            setTextColor(MUTED)
            setPadding(0, dp(14), 0, 0)
        })
        scroll.addView(root)
        setContentView(scroll)
        refreshHistory()
    }

    private fun refreshHistory() {
        historyOffset = 0
        loadHistoryPage(reset = true)
    }

    private fun loadOlderHistory() {
        if (historyLoading) return
        if (historyContainer.childCount > 0) {
            historyContainer.removeViewAt(historyContainer.childCount - 1)
        }
        loadHistoryPage(reset = false)
    }

    private fun loadHistoryPage(reset: Boolean) {
        if (!mainContentVisible || !::historyContainer.isInitialized || historyLoading) return
        if (reset) historyContainer.removeAllViews()
        if (!prefs.getBoolean("history_enabled", true)) {
            historyContainer.removeAllViews()
            historyContainer.addView(TextView(this).apply {
                text = "Typing history is turned off."
                textSize = 14f
                setTextColor(MUTED)
            })
            return
        }

        historyLoading = true
        if (reset) {
            historyContainer.addView(TextView(this).apply {
                text = "Loading saved words…"
                textSize = 14f
                setTextColor(MUTED)
            })
        }
        val requestedOffset = historyOffset
        HistoryStore.loadPageAsync(this, requestedOffset, HISTORY_PAGE_SIZE) { page ->
            historyLoading = false
            if (isFinishing || !mainContentVisible || !::historyContainer.isInitialized) return@loadPageAsync
            if (reset) historyContainer.removeAllViews()

            if (page.total < 0) {
                historyContainer.addView(TextView(this).apply {
                    text = "History load failed. Please reopen this screen."
                    textSize = 14f
                    setTextColor(MUTED)
                })
                return@loadPageAsync
            }
            if (page.total == 0 && historyOffset == 0) {
                historyContainer.addView(TextView(this).apply {
                    text = "No saved words yet. Your committed words will appear here."
                    textSize = 14f
                    setTextColor(MUTED)
                })
                return@loadPageAsync
            }

            page.wordsNewestFirst.forEachIndexed { index, word ->
                historyContainer.addView(TextView(this).apply {
                    text = "${requestedOffset + index + 1}.  $word"
                    textSize = 15f
                    setTextColor(TEXT)
                    setPadding(0, dp(6), 0, dp(6))
                })
                if (index != page.wordsNewestFirst.lastIndex) {
                    historyContainer.addView(View(this).apply {
                        setBackgroundColor(0xFFE8EAF0.toInt())
                    }, LinearLayout.LayoutParams(-1, dp(1)))
                }
            }
            historyOffset = requestedOffset + page.wordsNewestFirst.size
            if (historyOffset < page.total) {
                historyContainer.addView(actionButton("Load older words (${page.total - historyOffset} remaining)") {
                    loadOlderHistory()
                }, LinearLayout.LayoutParams(-1, dp(46)).apply { topMargin = dp(10) })
            }
        }
    }

    private fun brandLogo(size: Int) = ImageView(this).apply {
        setImageResource(R.drawable.logo_likho)
        scaleType = ImageView.ScaleType.FIT_CENTER
        contentDescription = "लिखो लोगो"
        layoutParams = LinearLayout.LayoutParams(dp(size), dp(size)).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            bottomMargin = dp(12)
        }
    }

    private fun actionButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 15f
        setTextColor(TEXT)
        background = roundedButton(Color.WHITE, KEY_BORDER)
        stateListAnimator = null
        setOnClickListener { action() }
    }

    private fun roundedButton(fill: Int, stroke: Int) = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(11).toFloat()
        if (stroke != Color.TRANSPARENT) setStroke(dp(1), stroke)
    }

    private fun fullWidth() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    private fun match() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply { bottomMargin = dp(10) }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val HISTORY_PAGE_SIZE = 100
        private val BG = 0xFFF0EFF6.toInt()
        private val NAVY = 0xFF1F3448.toInt()
        private val TEXT = 0xFF263746.toInt()
        private val MUTED = 0xFF687887.toInt()
        private val ERROR = 0xFFB3261E.toInt()
        private val KEY_BORDER = 0xFFE1E4EC.toInt()
    }
}

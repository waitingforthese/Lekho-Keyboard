package com.example.lekho.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("lekho_local", Context.MODE_PRIVATE) }
    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scroll = ScrollView(this).apply { setBackgroundColor(BG) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(30), dp(22), dp(24))
            setBackgroundColor(BG)
        }
        root.addView(TextView(this).apply {
            text = "Lekho Keyboard"
            textSize = 29f
            setTextColor(NAVY)
        })
        root.addView(TextView(this).apply {
            text = "Version 1.2 · Gboard-inspired development build"
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
        root.addView(actionButton("1. Enable Lekho Keyboard") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }, match())
        root.addView(actionButton("2. Choose Lekho Keyboard") {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showInputMethodPicker()
        }, match())

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
                if (!checked) prefs.edit().remove("history").apply()
                refreshHistory()
            }
        }, match())
        root.addView(actionButton("Clear saved typing history") {
            prefs.edit().remove("history").apply()
            refreshHistory()
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
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(12).toFloat()
            }
        }
        root.addView(historyContainer, match())
        root.addView(TextView(this).apply {
            text = "Only committed words are saved (up to 100). Password fields and editors that disable personalized learning are excluded. Clipboard content is read only when you tap Paste. History is stored on this device and can be disabled or cleared here."
            textSize = 13f
            setTextColor(MUTED)
            setPadding(0, dp(14), 0, 0)
        })
        scroll.addView(root)
        setContentView(scroll)
        refreshHistory()
    }

    override fun onResume() {
        super.onResume()
        if (::historyContainer.isInitialized) refreshHistory()
    }

    private fun refreshHistory() {
        if (!::historyContainer.isInitialized) return
        historyContainer.removeAllViews()
        if (!prefs.getBoolean("history_enabled", true)) {
            historyContainer.addView(TextView(this).apply {
                text = "Typing history is turned off."
                textSize = 14f
                setTextColor(MUTED)
            })
            return
        }
        val items = prefs.getString("history", "").orEmpty().lines().filter { it.isNotBlank() }.asReversed()
        if (items.isEmpty()) {
            historyContainer.addView(TextView(this).apply {
                text = "No saved words yet. Your committed words will appear here."
                textSize = 14f
                setTextColor(MUTED)
            })
            return
        }
        items.take(100).forEachIndexed { index, word ->
            historyContainer.addView(TextView(this).apply {
                text = "${index + 1}.  $word"
                textSize = 15f
                setTextColor(TEXT)
                setPadding(0, dp(6), 0, dp(6))
            })
            if (index != items.lastIndex) {
                historyContainer.addView(android.view.View(this).apply {
                    setBackgroundColor(0xFFE8EAF0.toInt())
                }, LinearLayout.LayoutParams(-1, dp(1)))
            }
        }
    }

    private fun actionButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        textSize = 15f
        setTextColor(TEXT)
        background = android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = dp(10).toFloat()
            setStroke(dp(1), 0xFFE1E4EC.toInt())
        }
        setOnClickListener { action() }
    }

    private fun match() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply { bottomMargin = dp(10) }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    companion object {
        private val BG = 0xFFF0EFF6.toInt()
        private val NAVY = 0xFF1F3448.toInt()
        private val TEXT = 0xFF263746.toInt()
        private val MUTED = 0xFF687887.toInt()
    }
}

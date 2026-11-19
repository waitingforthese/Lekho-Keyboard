package com.example.lekho.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("lekho_local", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(36), dp(24), dp(24))
            setBackgroundColor(0xFFF5F7FA.toInt())
        }
        root.addView(TextView(this).apply {
            text = "Lekho Keyboard"
            textSize = 28f
            setTextColor(0xFF102A43.toInt())
        })
        root.addView(TextView(this).apply {
            text = "Version 1 · Development build\n\nFast English + Marathi phonetic typing. Type Marathi words using English letters, then select a suggestion or press Space for supported words.\n\nTyping history stays on this device. Clipboard content is read only when you tap the clipboard shortcut. Password and no-personalized-learning fields are excluded from suggestions and history."
            textSize = 15f
            setPadding(0, dp(18), 0, dp(18))
            setTextColor(0xFF334E68.toInt())
        })
        root.addView(Button(this).apply {
            text = "1. Enable keyboard"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        }, match())
        root.addView(Button(this).apply {
            text = "2. Choose Lekho Keyboard"
            setOnClickListener {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showInputMethodPicker()
            }
        }, match())
        root.addView(Switch(this).apply {
            text = "Save typing history on this device"
            isChecked = prefs.getBoolean("history_enabled", true)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("history_enabled", checked).apply()
                if (!checked) prefs.edit().remove("history").apply()
            }
        }, match())
        root.addView(Button(this).apply {
            text = "Clear saved typing history"
            setOnClickListener { prefs.edit().remove("history").apply() }
        }, match())
        setContentView(root)
    }

    private fun match() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply { bottomMargin = dp(12) }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

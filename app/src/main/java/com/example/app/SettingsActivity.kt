package com.example.app

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val cardCountRadioGroup: RadioGroup = findViewById(R.id.cardCountRadioGroup)
        val backButton: Button = findViewById(R.id.backButton)
        val sharedPrefs = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE)

        val currentCardCount = sharedPrefs.getInt(Constants.KEY_CARD_COUNT, 8)
        when (currentCardCount) {
            8 -> cardCountRadioGroup.check(R.id.radio_8_cards)
            12 -> cardCountRadioGroup.check(R.id.radio_12_cards)
            16 -> cardCountRadioGroup.check(R.id.radio_16_cards)
        }

        cardCountRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedCount = when (checkedId) {
                R.id.radio_8_cards -> 8
                R.id.radio_12_cards -> 12
                R.id.radio_16_cards -> 16
                else -> 8
            }
            sharedPrefs.edit().putInt(Constants.KEY_CARD_COUNT, selectedCount).apply()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
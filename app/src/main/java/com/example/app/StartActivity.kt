package com.example.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val startButton: Button = findViewById(R.id.startButton)
        val settingsButton: Button = findViewById(R.id.settingsButton)

        startButton.setOnClickListener {
            val sharedPrefs = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE)
            val cardCount = sharedPrefs.getInt(Constants.KEY_CARD_COUNT, 8)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CARD_COUNT", cardCount)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
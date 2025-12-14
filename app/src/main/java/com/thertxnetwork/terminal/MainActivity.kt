package com.thertxnetwork.terminal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val themeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitch)
        
        // Check current theme
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        themeSwitch.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        // Set up theme switcher
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Set up button click listener
        findViewById<MaterialButton>(R.id.actionButton).setOnClickListener {
            // Button action placeholder
        }
    }
}

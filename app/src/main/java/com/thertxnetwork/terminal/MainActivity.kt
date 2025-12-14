package com.thertxnetwork.terminal

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Apply status bar color theme
        applyStatusBarTheme()

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

    private fun applyStatusBarTheme() {
        // Get the primary color from the current theme
        val primaryColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, 0)
        window.statusBarColor = primaryColor
        
        // Determine if we should use light or dark status bar icons
        // Light theme uses dark icons, dark theme uses light icons
        val isLightTheme = (resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK) != 
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        
        // For Android 11 (API 30) and above, use WindowInsetsController
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BAR,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BAR
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6.0 (API 23) and above
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (isLightTheme) {
                0 // Dark icons on light background
            } else {
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Light icons on dark background
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reapply status bar theme when activity resumes (e.g., after theme change)
        applyStatusBarTheme()
    }
}

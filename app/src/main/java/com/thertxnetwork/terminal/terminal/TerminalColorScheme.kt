package com.thertxnetwork.terminal.terminal

import android.graphics.Color

/**
 * Terminal color scheme supporting 256 colors and true color.
 */
class TerminalColorScheme {
    
    // Default foreground and background colors
    var defaultForeground = Color.WHITE
    var defaultBackground = Color.BLACK
    
    // Standard 16 ANSI colors
    private val ansiColors = intArrayOf(
        // Normal colors (0-7)
        Color.parseColor("#000000"), // Black
        Color.parseColor("#CD0000"), // Red
        Color.parseColor("#00CD00"), // Green
        Color.parseColor("#CDCD00"), // Yellow
        Color.parseColor("#0000EE"), // Blue
        Color.parseColor("#CD00CD"), // Magenta
        Color.parseColor("#00CDCD"), // Cyan
        Color.parseColor("#E5E5E5"), // White
        
        // Bright colors (8-15)
        Color.parseColor("#7F7F7F"), // Bright Black (Gray)
        Color.parseColor("#FF0000"), // Bright Red
        Color.parseColor("#00FF00"), // Bright Green
        Color.parseColor("#FFFF00"), // Bright Yellow
        Color.parseColor("#5C5CFF"), // Bright Blue
        Color.parseColor("#FF00FF"), // Bright Magenta
        Color.parseColor("#00FFFF"), // Bright Cyan
        Color.parseColor("#FFFFFF")  // Bright White
    )
    
    /**
     * Get color by index (0-255 for 256 color mode)
     */
    fun getColor(index: Int): Int {
        return when {
            index < 0 -> defaultForeground
            index < 16 -> ansiColors[index]
            index < 232 -> {
                // 216 colors (16-231): 6x6x6 color cube
                val idx = index - 16
                val r = (idx / 36) * 51
                val g = ((idx % 36) / 6) * 51
                val b = (idx % 6) * 51
                Color.rgb(r, g, b)
            }
            index < 256 -> {
                // 24 grayscale colors (232-255)
                val gray = 8 + (index - 232) * 10
                Color.rgb(gray, gray, gray)
            }
            else -> defaultForeground
        }
    }
    
    /**
     * Get RGB color from 24-bit true color values
     */
    fun getTrueColor(r: Int, g: Int, b: Int): Int {
        return Color.rgb(r, g, b)
    }
}

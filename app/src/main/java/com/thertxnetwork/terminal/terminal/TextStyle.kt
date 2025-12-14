package com.thertxnetwork.terminal.terminal

/**
 * Represents text style attributes for terminal characters.
 */
data class TextStyle(
    val foregroundColor: Int = -1, // -1 means use default
    val backgroundColor: Int = -1, // -1 means use default
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val blink: Boolean = false,
    val inverse: Boolean = false
) {
    companion object {
        val DEFAULT = TextStyle()
    }
    
    /**
     * Get the actual foreground color, applying inverse if needed
     */
    fun getEffectiveForeground(colorScheme: TerminalColorScheme): Int {
        return if (inverse) {
            if (backgroundColor >= 0) colorScheme.getColor(backgroundColor)
            else colorScheme.defaultBackground
        } else {
            if (foregroundColor >= 0) colorScheme.getColor(foregroundColor)
            else colorScheme.defaultForeground
        }
    }
    
    /**
     * Get the actual background color, applying inverse if needed
     */
    fun getEffectiveBackground(colorScheme: TerminalColorScheme): Int {
        return if (inverse) {
            if (foregroundColor >= 0) colorScheme.getColor(foregroundColor)
            else colorScheme.defaultForeground
        } else {
            if (backgroundColor >= 0) colorScheme.getColor(backgroundColor)
            else colorScheme.defaultBackground
        }
    }
}

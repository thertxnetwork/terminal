package com.thertxnetwork.terminal.terminal

/**
 * Terminal emulator that processes input and manages terminal state.
 * Handles basic VT100/ANSI escape sequences.
 */
class TerminalEmulator(cols: Int, rows: Int) {
    
    val buffer = TerminalBuffer(cols, rows)
    
    private var escapeSequenceBuilder = StringBuilder()
    private var inEscapeSequence = false
    
    interface TerminalListener {
        fun onBell()
        fun onTitleChanged(title: String)
        fun onScreenUpdated()
    }
    
    var listener: TerminalListener? = null
    
    /**
     * Process input from the terminal session
     */
    fun processInput(data: ByteArray, length: Int) {
        val text = String(data, 0, length, Charsets.UTF_8)
        processText(text)
        listener?.onScreenUpdated()
    }
    
    private fun processText(text: String) {
        var i = 0
        while (i < text.length) {
            val char = text[i]
            
            when {
                inEscapeSequence -> {
                    escapeSequenceBuilder.append(char)
                    if (isEscapeSequenceComplete(escapeSequenceBuilder.toString())) {
                        processEscapeSequence(escapeSequenceBuilder.toString())
                        escapeSequenceBuilder.clear()
                        inEscapeSequence = false
                    }
                }
                char == '\u001B' -> {
                    // ESC character - start escape sequence
                    inEscapeSequence = true
                    escapeSequenceBuilder.clear()
                    escapeSequenceBuilder.append(char)
                }
                char == '\u0007' -> {
                    // Bell character
                    listener?.onBell()
                }
                else -> {
                    // Regular character
                    buffer.appendText(char.toString())
                }
            }
            i++
        }
    }
    
    private fun isEscapeSequenceComplete(sequence: String): Boolean {
        if (sequence.length < 2) return false
        
        // CSI sequences: ESC [ ... letter
        if (sequence.startsWith("\u001B[")) {
            if (sequence.length >= 3) {
                val lastChar = sequence.last()
                // Check if it's a final character (letter or specific symbols)
                if (lastChar.isLetter() || lastChar in "@`~") {
                    return true
                }
            }
            return false
        }
        
        // OSC sequences: ESC ] ... ESC \ or BEL
        if (sequence.startsWith("\u001B]")) {
            return sequence.endsWith("\u001B\\") || sequence.endsWith("\u0007")
        }
        
        // Simple two-character escapes
        if (sequence.length >= 2) {
            val secondChar = sequence[1]
            // ESC followed by specific characters
            if (secondChar in "78DEHM=>c") {
                return true
            }
        }
        
        return false
    }
    
    private fun processEscapeSequence(sequence: String) {
        when {
            // CSI sequences: ESC [ ...
            sequence.startsWith("\u001B[") -> {
                processCsiSequence(sequence.substring(2))
            }
            // OSC sequences: ESC ] ...
            sequence.startsWith("\u001B]") -> {
                processOscSequence(sequence)
            }
            // Simple escapes
            sequence == "\u001B7" -> {
                // Save cursor position (not implemented in simple version)
            }
            sequence == "\u001B8" -> {
                // Restore cursor position (not implemented in simple version)
            }
            sequence == "\u001Bc" -> {
                // Reset terminal
                buffer.clear()
            }
            sequence == "\u001BM" -> {
                // Reverse index (move up)
            }
        }
    }
    
    private fun processCsiSequence(csi: String) {
        if (csi.isEmpty()) return
        
        val command = csi.last()
        val paramStr = if (csi.length > 1) csi.substring(0, csi.length - 1) else ""
        val params = if (paramStr.isNotEmpty()) {
            paramStr.split(';').mapNotNull { it.toIntOrNull() }
        } else {
            emptyList()
        }
        
        when (command) {
            'H', 'f' -> {
                // Cursor position
                val row = params.getOrElse(0) { 1 } - 1
                val col = params.getOrElse(1) { 1 } - 1
                buffer.setCursor(row, col)
            }
            'A' -> {
                // Cursor up
                val count = params.getOrElse(0) { 1 }
                buffer.setCursor(buffer.cursorRow - count, buffer.cursorCol)
            }
            'B' -> {
                // Cursor down
                val count = params.getOrElse(0) { 1 }
                buffer.setCursor(buffer.cursorRow + count, buffer.cursorCol)
            }
            'C' -> {
                // Cursor forward
                val count = params.getOrElse(0) { 1 }
                buffer.setCursor(buffer.cursorRow, buffer.cursorCol + count)
            }
            'D' -> {
                // Cursor back
                val count = params.getOrElse(0) { 1 }
                buffer.setCursor(buffer.cursorRow, buffer.cursorCol - count)
            }
            'J' -> {
                // Erase display
                val mode = params.getOrElse(0) { 0 }
                if (mode == 2) {
                    buffer.clear()
                }
            }
            'K' -> {
                // Erase line (simplified)
            }
            'm' -> {
                // Set graphics mode (colors, bold, etc.)
                processSGR(params)
            }
        }
    }
    
    private fun processSGR(params: List<Int>) {
        var currentStyle = buffer.currentStyle
        var i = 0
        
        while (i < params.size) {
            val param = params[i]
            
            when (param) {
                0 -> currentStyle = TextStyle.DEFAULT // Reset
                1 -> currentStyle = currentStyle.copy(bold = true)
                3 -> currentStyle = currentStyle.copy(italic = true)
                4 -> currentStyle = currentStyle.copy(underline = true)
                5, 6 -> currentStyle = currentStyle.copy(blink = true)
                7 -> currentStyle = currentStyle.copy(inverse = true)
                9 -> currentStyle = currentStyle.copy(strikethrough = true)
                22 -> currentStyle = currentStyle.copy(bold = false)
                23 -> currentStyle = currentStyle.copy(italic = false)
                24 -> currentStyle = currentStyle.copy(underline = false)
                25 -> currentStyle = currentStyle.copy(blink = false)
                27 -> currentStyle = currentStyle.copy(inverse = false)
                29 -> currentStyle = currentStyle.copy(strikethrough = false)
                
                // Foreground colors (30-37, 90-97)
                in 30..37 -> currentStyle = currentStyle.copy(foregroundColor = param - 30)
                in 90..97 -> currentStyle = currentStyle.copy(foregroundColor = param - 90 + 8)
                
                // Background colors (40-47, 100-107)
                in 40..47 -> currentStyle = currentStyle.copy(backgroundColor = param - 40)
                in 100..107 -> currentStyle = currentStyle.copy(backgroundColor = param - 100 + 8)
                
                // 256 color mode and true color
                38, 48 -> {
                    if (i + 2 < params.size && params[i + 1] == 5) {
                        // 256-color mode: ESC[38;5;Nm or ESC[48;5;Nm
                        val color = params[i + 2]
                        if (param == 38) {
                            currentStyle = currentStyle.copy(foregroundColor = color)
                        } else {
                            currentStyle = currentStyle.copy(backgroundColor = color)
                        }
                        i += 2
                    } else if (i + 4 < params.size && params[i + 1] == 2) {
                        // True color (24-bit RGB): ESC[38;2;R;G;Bm or ESC[48;2;R;G;Bm
                        // Note: Currently not fully supported - we map to closest 256-color
                        // TODO: Implement true color support by storing RGB values directly
                        // For now, skip the RGB parameters to avoid parsing errors
                        i += 4
                    }
                }
                
                // Default colors
                39 -> currentStyle = currentStyle.copy(foregroundColor = -1)
                49 -> currentStyle = currentStyle.copy(backgroundColor = -1)
            }
            
            i++
        }
        
        buffer.currentStyle = currentStyle
    }
    
    private fun processOscSequence(sequence: String) {
        // OSC sequences for setting title, etc.
        // Format: ESC ] number ; text ESC \ or BEL
        val content = sequence.substring(2).removeSuffix("\u001B\\").removeSuffix("\u0007")
        val parts = content.split(';', limit = 2)
        
        if (parts.size == 2) {
            val code = parts[0].toIntOrNull()
            val text = parts[1]
            
            when (code) {
                0, 1, 2 -> {
                    // Set window title
                    listener?.onTitleChanged(text)
                }
            }
        }
    }
    
    fun resize(newCols: Int, newRows: Int) {
        buffer.resize(newCols, newRows)
    }
}

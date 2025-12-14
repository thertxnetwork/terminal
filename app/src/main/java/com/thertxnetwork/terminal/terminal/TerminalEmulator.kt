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
    }
    
    var listener: TerminalListener? = null
    
    /**
     * Process input from the terminal session
     */
    fun processInput(data: ByteArray, length: Int) {
        val text = String(data, 0, length, Charsets.UTF_8)
        processText(text)
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
        val params = if (csi.length > 1) {
            csi.substring(0, csi.length - 1).split(';')
                .mapNotNull { it.toIntOrNull() }
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
                // Set graphics mode (colors, bold, etc.) - simplified
            }
        }
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

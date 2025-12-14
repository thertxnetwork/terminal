package com.thertxnetwork.terminal.terminal

/**
 * Manages the terminal screen buffer, storing lines of text and cursor position.
 */
class TerminalBuffer(var cols: Int, var rows: Int) {
    
    private val lines = mutableListOf<StringBuilder>()
    var cursorRow = 0
        private set
    var cursorCol = 0
        private set
    
    init {
        // Initialize empty lines
        for (i in 0 until rows) {
            lines.add(StringBuilder())
        }
    }
    
    fun resize(newCols: Int, newRows: Int) {
        if (newCols == cols && newRows == rows) return
        
        cols = newCols
        rows = newRows
        
        // Adjust number of lines
        while (lines.size < rows) {
            lines.add(StringBuilder())
        }
        while (lines.size > rows) {
            lines.removeAt(lines.size - 1)
        }
        
        // Ensure cursor is in bounds
        cursorRow = cursorRow.coerceIn(0, rows - 1)
        cursorCol = cursorCol.coerceIn(0, cols - 1)
    }
    
    fun getLine(row: Int): String {
        if (row < 0 || row >= lines.size) return ""
        return lines[row].toString()
    }
    
    fun appendText(text: String) {
        for (char in text) {
            when (char) {
                '\n' -> {
                    // New line
                    cursorCol = 0
                    cursorRow++
                    if (cursorRow >= rows) {
                        // Scroll up
                        lines.removeAt(0)
                        lines.add(StringBuilder())
                        cursorRow = rows - 1
                    }
                }
                '\r' -> {
                    // Carriage return
                    cursorCol = 0
                }
                '\b' -> {
                    // Backspace
                    if (cursorCol > 0) {
                        cursorCol--
                        if (cursorCol < lines[cursorRow].length) {
                            lines[cursorRow].deleteCharAt(cursorCol)
                        }
                    }
                }
                '\t' -> {
                    // Tab - move to next 8-character boundary
                    val nextTab = ((cursorCol / 8) + 1) * 8
                    cursorCol = nextTab.coerceAtMost(cols - 1)
                }
                else -> {
                    if (char.isISOControl()) {
                        // Skip other control characters for now
                        continue
                    }
                    
                    // Ensure line is long enough
                    val line = lines[cursorRow]
                    while (line.length < cursorCol) {
                        line.append(' ')
                    }
                    
                    // Insert or replace character
                    if (cursorCol < line.length) {
                        line[cursorCol] = char
                    } else {
                        line.append(char)
                    }
                    
                    cursorCol++
                    
                    // Wrap to next line if needed
                    if (cursorCol >= cols) {
                        cursorCol = 0
                        cursorRow++
                        if (cursorRow >= rows) {
                            // Scroll up
                            lines.removeAt(0)
                            lines.add(StringBuilder())
                            cursorRow = rows - 1
                        }
                    }
                }
            }
        }
    }
    
    fun clear() {
        lines.clear()
        for (i in 0 until rows) {
            lines.add(StringBuilder())
        }
        cursorRow = 0
        cursorCol = 0
    }
    
    fun setCursor(row: Int, col: Int) {
        cursorRow = row.coerceIn(0, rows - 1)
        cursorCol = col.coerceIn(0, cols - 1)
    }
}

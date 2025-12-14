package com.thertxnetwork.terminal.terminal

/**
 * Represents a single character with its style in the terminal.
 */
data class StyledChar(
    val char: Char,
    val style: TextStyle = TextStyle.DEFAULT
)

/**
 * Manages the terminal screen buffer, storing lines of text with styles and cursor position.
 */
class TerminalBuffer(var cols: Int, var rows: Int) {
    
    companion object {
        private const val TAB_STOP = 8
    }
    
    private val lines = mutableListOf<MutableList<StyledChar>>()
    var cursorRow = 0
        private set
    var cursorCol = 0
        private set
    
    var currentStyle = TextStyle.DEFAULT
    
    init {
        // Initialize empty lines
        for (i in 0 until rows) {
            lines.add(mutableListOf())
        }
    }
    
    fun resize(newCols: Int, newRows: Int) {
        if (newCols == cols && newRows == rows) return
        
        cols = newCols
        rows = newRows
        
        // Adjust number of lines
        while (lines.size < rows) {
            lines.add(mutableListOf())
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
        return lines[row].map { it.char }.joinToString("")
    }
    
    fun getStyledLine(row: Int): List<StyledChar> {
        if (row < 0 || row >= lines.size) return emptyList()
        return lines[row]
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
                        lines.add(mutableListOf())
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
                        if (cursorCol < lines[cursorRow].size) {
                            lines[cursorRow].removeAt(cursorCol)
                        }
                    }
                }
                '\t' -> {
                    // Tab - move to next TAB_STOP-character boundary
                    val nextTab = ((cursorCol / TAB_STOP) + 1) * TAB_STOP
                    cursorCol = nextTab.coerceAtMost(cols - 1)
                }
                else -> {
                    if (char.isISOControl()) {
                        // Skip other control characters for now
                        continue
                    }
                    
                    // Ensure line is long enough
                    val line = lines[cursorRow]
                    while (line.size < cursorCol) {
                        line.add(StyledChar(' ', currentStyle))
                    }
                    
                    // Insert or replace character with current style
                    val styledChar = StyledChar(char, currentStyle)
                    if (cursorCol < line.size) {
                        line[cursorCol] = styledChar
                    } else {
                        line.add(styledChar)
                    }
                    
                    cursorCol++
                    
                    // Wrap to next line if needed
                    if (cursorCol >= cols) {
                        cursorCol = 0
                        cursorRow++
                        if (cursorRow >= rows) {
                            // Scroll up
                            lines.removeAt(0)
                            lines.add(mutableListOf())
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
            lines.add(mutableListOf())
        }
        cursorRow = 0
        cursorCol = 0
    }
    
    fun setCursor(row: Int, col: Int) {
        cursorRow = row.coerceIn(0, rows - 1)
        cursorCol = col.coerceIn(0, cols - 1)
    }
}

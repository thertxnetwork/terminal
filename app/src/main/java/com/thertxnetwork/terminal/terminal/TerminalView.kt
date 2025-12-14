package com.thertxnetwork.terminal.terminal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View

/**
 * A custom view that renders terminal output with text and colors.
 * Supports VT100/ANSI escape sequences for terminal emulation.
 */
class TerminalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textSize = 40f
        color = 0xFFFFFFFF.toInt() // White text
    }

    private val backgroundPaint = Paint().apply {
        color = 0xFF000000.toInt() // Black background
    }

    private var terminalBuffer: TerminalBuffer = TerminalBuffer(80, 24)
    private var terminalSession: TerminalSession? = null
    private var charWidth = 0f
    private var charHeight = 0f
    private var charDescent = 0f

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        measureTextDimensions()
    }

    private fun measureTextDimensions() {
        val metrics = textPaint.fontMetrics
        charWidth = textPaint.measureText("M")
        charHeight = metrics.descent - metrics.ascent
        charDescent = metrics.descent
    }

    fun setTerminalSession(session: TerminalSession) {
        this.terminalSession = session
        invalidate()
    }

    fun setTerminalEmulator(emulator: TerminalEmulator) {
        terminalBuffer = emulator.buffer
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        
        // Calculate how many columns and rows fit
        if (charWidth > 0 && charHeight > 0) {
            val cols = (width / charWidth).toInt()
            val rows = (height / charHeight).toInt()
            
            if (cols > 0 && rows > 0) {
                terminalBuffer.resize(cols, rows)
            }
        }
        
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        
        // Draw terminal text
        val rows = terminalBuffer.rows
        val cols = terminalBuffer.cols
        
        for (row in 0 until rows) {
            val line = terminalBuffer.getLine(row)
            if (line.isNotEmpty()) {
                val y = (row + 1) * charHeight - charDescent
                canvas.drawText(line, 0f, y, textPaint)
            }
        }
        
        // Draw cursor
        val cursorRow = terminalBuffer.cursorRow
        val cursorCol = terminalBuffer.cursorCol
        val cursorX = cursorCol * charWidth
        val cursorY = cursorRow * charHeight
        
        val cursorPaint = Paint().apply {
            color = 0xFF00FF00.toInt() // Green cursor
        }
        canvas.drawRect(
            cursorX,
            cursorY,
            cursorX + charWidth,
            cursorY + charHeight,
            cursorPaint
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // Handle key events and send to terminal
        val session = terminalSession
        if (session != null && session.isRunning()) {
            // Check for character input
            val unicodeChar = event.unicodeChar
            if (unicodeChar != 0) {
                // Regular character
                val char = unicodeChar.toChar()
                session.write(char.toString())
                return true
            } else {
                // Special key
                val ctrlDown = event.isCtrlPressed
                val altDown = event.isAltPressed
                session.writeKeyEvent(keyCode, ctrlDown, altDown)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun appendText(text: String) {
        terminalBuffer.appendText(text)
        invalidate()
    }
}

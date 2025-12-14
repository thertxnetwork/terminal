package com.thertxnetwork.terminal.terminal

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.*

/**
 * Manages a terminal session with a shell process.
 * Handles communication between the shell and the terminal emulator.
 */
class TerminalSession(
    private val emulator: TerminalEmulator,
    private val shellPath: String = "/system/bin/sh",
    private val homeDir: String? = null
) {
    
    private var process: Process? = null
    private var outputReader: Thread? = null
    private var running = false
    private val handler = Handler(Looper.getMainLooper())
    
    interface SessionListener {
        fun onSessionFinished(exitCode: Int)
    }
    
    var listener: SessionListener? = null
    
    /**
     * Start the terminal session and shell process
     */
    fun start() {
        if (running) return
        
        try {
            // Start shell process
            val processBuilder = ProcessBuilder(shellPath)
            processBuilder.redirectErrorStream(true)
            
            // Set environment variables
            val env = processBuilder.environment()
            env["TERM"] = "xterm-256color"
            env["HOME"] = homeDir ?: "/data/data/com.thertxnetwork.terminal"
            env["PATH"] = "/system/bin:/system/xbin"
            
            process = processBuilder.start()
            running = true
            
            Log.d(TAG, "Terminal session started with shell: $shellPath")
            
            // Start thread to read output from shell
            outputReader = Thread {
                readShellOutput()
            }
            outputReader?.start()
            
            // Monitor process exit
            Thread {
                try {
                    val exitCode = process?.waitFor() ?: -1
                    running = false
                    handler.post {
                        listener?.onSessionFinished(exitCode)
                    }
                    Log.d(TAG, "Shell process exited with code: $exitCode")
                } catch (e: InterruptedException) {
                    Log.e(TAG, "Process wait interrupted", e)
                }
            }.start()
            
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start shell process", e)
            running = false
        }
    }
    
    private fun readShellOutput() {
        try {
            val input = process?.inputStream ?: return
            val buffer = ByteArray(4096)
            
            while (running) {
                val bytesRead = input.read(buffer)
                if (bytesRead <= 0) break
                
                // Process output on main thread
                handler.post {
                    emulator.processInput(buffer, bytesRead)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading shell output", e)
        }
    }
    
    /**
     * Write input to the shell process
     */
    fun write(data: String) {
        try {
            val output = process?.outputStream
            output?.write(data.toByteArray(Charsets.UTF_8))
            output?.flush()
        } catch (e: IOException) {
            Log.e(TAG, "Error writing to shell", e)
        }
    }
    
    /**
     * Write a key press to the shell
     */
    fun writeKeyEvent(keyCode: Int, ctrlDown: Boolean, altDown: Boolean) {
        // Map key codes to terminal sequences
        val sequence = when (keyCode) {
            android.view.KeyEvent.KEYCODE_ENTER -> "\r"
            android.view.KeyEvent.KEYCODE_DEL -> "\b"
            android.view.KeyEvent.KEYCODE_TAB -> "\t"
            android.view.KeyEvent.KEYCODE_DPAD_UP -> "\u001B[A"
            android.view.KeyEvent.KEYCODE_DPAD_DOWN -> "\u001B[B"
            android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> "\u001B[C"
            android.view.KeyEvent.KEYCODE_DPAD_LEFT -> "\u001B[D"
            else -> null
        }
        
        sequence?.let { write(it) }
    }
    
    /**
     * Resize the terminal
     */
    fun resize(cols: Int, rows: Int) {
        emulator.resize(cols, rows)
    }
    
    /**
     * Close the terminal session
     */
    fun close() {
        running = false
        
        try {
            process?.destroy()
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying process", e)
        }
        
        try {
            outputReader?.interrupt()
            outputReader?.join(1000)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping output reader", e)
        }
        
        process = null
        outputReader = null
    }
    
    fun isRunning(): Boolean = running
    
    companion object {
        private const val TAG = "TerminalSession"
    }
}

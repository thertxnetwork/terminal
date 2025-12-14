package com.thertxnetwork.terminal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thertxnetwork.terminal.terminal.TerminalEmulator
import com.thertxnetwork.terminal.terminal.TerminalSession
import com.thertxnetwork.terminal.terminal.TerminalView

class MainActivity : AppCompatActivity() {
    
    private lateinit var terminalView: TerminalView
    private lateinit var terminalEmulator: TerminalEmulator
    private lateinit var terminalSession: TerminalSession
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create terminal view programmatically
        terminalView = TerminalView(this)
        setContentView(terminalView)
        
        // Initialize terminal emulator
        terminalEmulator = TerminalEmulator(80, 24)
        terminalView.setTerminalEmulator(terminalEmulator)
        
        // Create and start terminal session
        terminalSession = TerminalSession(terminalEmulator)
        terminalView.setTerminalSession(terminalSession)
        
        terminalSession.listener = object : TerminalSession.SessionListener {
            override fun onSessionFinished(exitCode: Int) {
                runOnUiThread {
                    terminalView.appendText("\n[Process completed with exit code $exitCode]")
                }
            }
        }
        
        terminalEmulator.listener = object : TerminalEmulator.TerminalListener {
            override fun onBell() {
                // Handle bell/beep sound
            }
            
            override fun onTitleChanged(title: String) {
                runOnUiThread {
                    setTitle(title)
                }
            }
            
            override fun onScreenUpdated() {
                runOnUiThread {
                    terminalView.invalidate()
                }
            }
        }
        
        // Start the terminal session
        terminalSession.start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        terminalSession.close()
    }
}

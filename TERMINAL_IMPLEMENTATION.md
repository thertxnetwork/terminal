# Terminal Implementation

This document describes the newly created terminal emulator implementation for the Terminal Android app.

## Overview

The terminal implementation is a **newly created** codebase that references the architecture of NeoTerm but is written from scratch with modern Kotlin practices. It provides a working terminal emulator with VT100/ANSI escape sequence support.

## Architecture

### Core Components

#### 1. TerminalEmulator
Located: `com.thertxnetwork.terminal.terminal.TerminalEmulator`

The main terminal emulator class that processes input and manages terminal state.

**Features:**
- VT100/ANSI escape sequence processing
- CSI (Control Sequence Introducer) command handling
- OSC (Operating System Command) sequence support
- SGR (Set Graphics Rendition) for colors and text styling
- Cursor positioning and movement commands

**Supported Escape Sequences:**
- `ESC [ H` - Cursor position
- `ESC [ A/B/C/D` - Cursor movement (up/down/right/left)
- `ESC [ J` - Erase display
- `ESC [ m` - Set graphics mode (colors, bold, italic, etc.)
- `ESC ] 0;title BEL` - Set window title

#### 2. TerminalSession
Located: `com.thertxnetwork.terminal.terminal.TerminalSession`

Manages the shell process and I/O communication with the terminal emulator.

**Features:**
- Starts and manages shell subprocess (`/system/bin/sh`)
- Handles input/output streams
- Thread-based output reading
- Process lifecycle management
- Key event to escape sequence mapping

**Environment:**
- `TERM=xterm-256color`
- `HOME=/data/data/com.thertxnetwork.terminal`
- `PATH=/system/bin:/system/xbin`

#### 3. TerminalBuffer
Located: `com.thertxnetwork.terminal.terminal.TerminalBuffer`

Manages the terminal screen buffer with styled characters.

**Features:**
- Dynamic resizing based on view dimensions
- Line wrapping and scrolling
- Cursor positioning
- Character-level styling support
- Control character handling (newline, tab, backspace, etc.)

**Data Structure:**
```kotlin
data class StyledChar(
    val char: Char,
    val style: TextStyle
)
```

#### 4. TerminalView
Located: `com.thertxnetwork.terminal.terminal.TerminalView`

Custom Android View that renders the terminal display.

**Features:**
- Character-by-character rendering with individual colors
- Monospace font rendering
- Cursor display
- Keyboard input handling
- Dynamic text sizing and column/row calculation
- Hardware keyboard support

**Rendering:**
- Uses Canvas API for drawing
- Renders styled characters with proper foreground/background colors
- Supports text attributes (bold, italic, underline, strikethrough)
- Smooth cursor rendering

#### 5. TextStyle
Located: `com.thertxnetwork.terminal.terminal.TextStyle`

Represents text styling attributes for terminal characters.

**Attributes:**
- `foregroundColor`: Color index (-1 for default)
- `backgroundColor`: Color index (-1 for default)
- `bold`: Bold text
- `italic`: Italic text
- `underline`: Underlined text
- `strikethrough`: Strikethrough text
- `blink`: Blinking text
- `inverse`: Inverse video (swap fg/bg colors)

#### 6. TerminalColorScheme
Located: `com.thertxnetwork.terminal.terminal.TerminalColorScheme`

Manages terminal color palette.

**Color Support:**
- 16 ANSI colors (8 normal + 8 bright)
- 256-color mode support
  - 16 ANSI colors
  - 216-color cube (6x6x6)
  - 24-level grayscale
- True color preparation (24-bit RGB)

## Terminal Features

### Implemented

✅ **Basic Terminal Emulation**
- Character display and input
- Cursor positioning
- Line wrapping and scrolling
- Control characters (newline, tab, backspace)

✅ **ANSI Escape Sequences**
- Cursor movement (up, down, left, right)
- Cursor positioning
- Screen clearing
- Text styling (bold, italic, underline)

✅ **Color Support**
- 16 ANSI colors
- 256-color mode
- SGR color codes (30-37, 90-97 for foreground)
- SGR color codes (40-47, 100-107 for background)
- Inverse video mode

✅ **Text Attributes**
- Bold
- Italic
- Underline
- Strikethrough
- Inverse colors

✅ **Keyboard Support**
- Character input
- Special keys (Enter, Backspace, Tab)
- Arrow keys (Up, Down, Left, Right)
- Control and Alt modifiers

### To Be Implemented

⏳ **Text Selection**
- Long press to enter selection mode
- Drag to select text
- Copy to clipboard

⏳ **Scrollback Buffer**
- Save scrolled-out lines
- Touch scrolling
- Scroll position indicator

⏳ **Additional Features**
- Paste from clipboard
- Hardware keyboard shortcuts
- Font size adjustment
- Color scheme customization
- Split screen support

## Usage

### Basic Setup

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var terminalView: TerminalView
    private lateinit var terminalEmulator: TerminalEmulator
    private lateinit var terminalSession: TerminalSession
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create terminal view
        terminalView = TerminalView(this)
        setContentView(terminalView)
        
        // Initialize emulator
        terminalEmulator = TerminalEmulator(80, 24)
        terminalView.setTerminalEmulator(terminalEmulator)
        
        // Create session
        terminalSession = TerminalSession(terminalEmulator)
        terminalView.setTerminalSession(terminalSession)
        
        // Set up listeners
        terminalSession.listener = object : TerminalSession.SessionListener {
            override fun onSessionFinished(exitCode: Int) {
                // Handle session end
            }
        }
        
        terminalEmulator.listener = object : TerminalEmulator.TerminalListener {
            override fun onBell() { }
            override fun onTitleChanged(title: String) {
                setTitle(title)
            }
            override fun onScreenUpdated() {
                terminalView.invalidate()
            }
        }
        
        // Start terminal
        terminalSession.start()
    }
}
```

## Comparison with NeoTerm

This implementation is **newly created** and differs from NeoTerm in several ways:

### Similarities (Architecture Reference)
- Similar component separation (Emulator, Session, View, Buffer)
- VT100/ANSI escape sequence processing approach
- Screen buffer management concept

### Differences (New Implementation)
- **Language**: Pure Kotlin vs. mixed Java/Kotlin
- **Simplicity**: Simplified implementation focused on core features
- **Data Structures**: Kotlin data classes and collections
- **Styling**: Integrated TextStyle system vs. separate handling
- **View Rendering**: Simplified Canvas-based rendering
- **No JNI**: Uses Java ProcessBuilder instead of native PTY
- **Modern APIs**: Uses modern Android and Kotlin features

## Technical Details

### Performance Considerations

1. **Rendering**: Character-by-character rendering with individual paint settings
2. **Input Processing**: ByteArray processing with UTF-8 encoding
3. **Threading**: Separate thread for shell output reading
4. **Main Thread**: UI updates posted to main thread via Handler

### Limitations

1. **PTY Support**: Uses ProcessBuilder instead of native PTY
   - No proper terminal size signaling (SIGWINCH)
   - Limited terminal control
   - May not work with all shell programs

2. **Shell**: Currently hardcoded to `/system/bin/sh`
   - Should work on most Android devices
   - Limited compared to full terminal apps

3. **Permissions**: Requires appropriate file system access
   - Works within app's sandbox
   - Limited access to system directories

## Future Enhancements

1. **Native PTY Support**: Implement JNI layer for proper PTY handling
2. **Extended Escape Sequences**: More complete VT100/xterm support
3. **Text Selection**: Copy/paste functionality
4. **Scrollback**: History buffer for scrolled content
5. **Configuration**: User-customizable colors, fonts, and behavior
6. **Multiple Sessions**: Tab or split-screen support
7. **Gesture Support**: Touch gestures for common operations

## References

The architecture was inspired by NeoTerm, but this is a completely new implementation:
- [NeoTerm Repository](https://github.com/NeoTerrm/NeoTerm)
- [xterm Control Sequences](https://invisible-island.net/xterm/ctlseqs/ctlseqs.html)
- [ANSI Escape Codes](https://en.wikipedia.org/wiki/ANSI_escape_code)
- [VT100 User Guide](https://vt100.net/docs/vt100-ug/)

# Terminal Android App

A modern Android terminal emulator built with Kotlin and Material Design 3. Features a fully functional terminal with VT100/ANSI escape sequence support, 256-color mode, and text styling.

## Features

- **Full Terminal Emulation**: VT100/ANSI escape sequence support
- **256 Color Support**: Full 256-color palette with SGR codes
- **Text Styling**: Bold, italic, underline, strikethrough, and inverse video
- **Modern UI**: Clean, monospace terminal display
- **Shell Integration**: Runs `/system/bin/sh` with proper I/O handling
- **Keyboard Support**: Hardware keyboard with special keys and modifiers
- **Dynamic Sizing**: Automatically calculates terminal dimensions

## Terminal Implementation

This app includes a **newly created** terminal emulator implementation that references the architecture of NeoTerm but is written from scratch in modern Kotlin.

### Core Components

- **TerminalEmulator**: Processes VT100/ANSI escape sequences
- **TerminalSession**: Manages shell process and I/O
- **TerminalBuffer**: Maintains screen buffer with styled characters
- **TerminalView**: Custom Android view for rendering terminal
- **TerminalColorScheme**: 256-color palette management
- **TextStyle**: Character-level styling attributes

See [TERMINAL_IMPLEMENTATION.md](TERMINAL_IMPLEMENTATION.md) for detailed documentation.

## Tech Stack

- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Build System**: Gradle
- **UI**: Custom Canvas-based rendering

## Building the Project

### Prerequisites

- Android Studio (latest version recommended)
- JDK 17 or higher
- Android SDK with API 34

### Build Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/thertxnetwork/terminal.git
   cd terminal
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build and run the app on an emulator or physical device

Alternatively, build from command line:
```bash
./gradlew assembleDebug
```

## How It Works

1. **MainActivity** creates and initializes the terminal components
2. **TerminalSession** starts a shell process and reads output
3. **TerminalEmulator** processes escape sequences and updates the buffer
4. **TerminalView** renders the buffer content with colors and styles
5. User input from keyboard is sent to the shell process

## Supported Features

### ANSI Escape Sequences

- ✅ Cursor positioning and movement
- ✅ Screen clearing
- ✅ Text colors (16 ANSI + 256 color mode)
- ✅ Text styling (bold, italic, underline, etc.)
- ✅ Inverse video
- ✅ Window title setting

### Keyboard

- ✅ Character input
- ✅ Special keys (Enter, Tab, Backspace)
- ✅ Arrow keys
- ✅ Control and Alt modifiers

## Future Enhancements

- [ ] Native PTY support via JNI
- [ ] Text selection and copy/paste
- [ ] Scrollback buffer
- [ ] Multiple terminal tabs
- [ ] Customizable color schemes
- [ ] Font size adjustment
- [ ] Hardware keyboard shortcuts

## Credits

Architecture inspired by [NeoTerm](https://github.com/NeoTerrm/NeoTerm) - an older Android terminal app. This implementation is completely new and written from scratch with modern Kotlin practices.

## License

This project is open source and available under the MIT License.

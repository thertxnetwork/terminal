# Terminal Demo - Color Test

This file demonstrates the terminal emulator's color capabilities.

## Running the Demo

Once the app is built and running, you can test the colors by typing:

```bash
# Test basic colors
echo -e "\e[31mRed text\e[0m"
echo -e "\e[32mGreen text\e[0m"
echo -e "\e[33mYellow text\e[0m"
echo -e "\e[34mBlue text\e[0m"
echo -e "\e[35mMagenta text\e[0m"
echo -e "\e[36mCyan text\e[0m"

# Test bright colors
echo -e "\e[91mBright red text\e[0m"
echo -e "\e[92mBright green text\e[0m"
echo -e "\e[93mBright yellow text\e[0m"
echo -e "\e[94mBright blue text\e[0m"

# Test background colors
echo -e "\e[41mRed background\e[0m"
echo -e "\e[42mGreen background\e[0m"
echo -e "\e[44mBlue background\e[0m"

# Test text styles
echo -e "\e[1mBold text\e[0m"
echo -e "\e[3mItalic text\e[0m"
echo -e "\e[4mUnderlined text\e[0m"
echo -e "\e[9mStrikethrough text\e[0m"

# Test combined styles
echo -e "\e[1;31mBold red text\e[0m"
echo -e "\e[4;32mUnderlined green text\e[0m"
echo -e "\e[1;4;33mBold underlined yellow text\e[0m"

# Test inverse video
echo -e "\e[7mInverse video\e[0m"

# Test 256 colors
echo -e "\e[38;5;196mColor 196\e[0m"
echo -e "\e[38;5;27mColor 27\e[0m"
echo -e "\e[38;5;226mColor 226\e[0m"
```

## Sample Commands

```bash
# List files
ls -la

# Check current directory
pwd

# Display environment variables
env

# Show date and time
date

# Display system info
uname -a

# Test tab completion
ls -l /sys[TAB]

# Test cursor movement
# Use arrow keys to navigate command history
```

## Keyboard Shortcuts

- **Enter**: Execute command
- **Backspace**: Delete character
- **Tab**: Tab character (8 spaces)
- **Arrow Keys**: Cursor navigation (sends escape sequences)
- **Ctrl + C**: Interrupt signal (if terminal supports it)
- **Ctrl + D**: EOF signal

## Expected Output

When you run the color test commands, you should see:
- Different colored text (red, green, blue, etc.)
- Text with different styles (bold, italic, underline)
- Background colors
- Inverse video mode
- 256-color palette colors

## Limitations

Currently using ProcessBuilder instead of native PTY:
- May not handle all terminal applications properly
- Signal handling is limited
- Terminal resizing may not be fully supported
- Some interactive programs may not work correctly

## Testing

The terminal should work with:
- ✅ Basic shell commands (ls, pwd, cd, echo)
- ✅ ANSI color codes
- ✅ Text styling
- ✅ Simple shell scripts
- ⚠️ Interactive programs (may have issues)
- ⚠️ Programs requiring proper PTY (vim, nano, top)

For full terminal functionality, native PTY support via JNI would be needed.

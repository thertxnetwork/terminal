# Terminal Android App - Technical Documentation

## Overview
This is a simple Android application built with Kotlin, implementing Material Design 3 with support for both light and dark themes.

## Key Features

### 1. Material Design 3 Implementation
The app uses the latest Material Design 3 (Material You) guidelines with:
- Complete Material 3 color system
- Modern typography scale
- Elevation and shadow system
- Rounded corners and modern shapes

### 2. Theme Support
- **Light Theme**: Bright, clean interface with purple accent color
- **Dark Theme**: Dark background for reduced eye strain
- **Theme Switcher**: Users can toggle between themes at runtime

### 3. UI Components
The app demonstrates several Material Design 3 components:
- `MaterialCardView`: Elevated cards with rounded corners
- `MaterialButton`: Filled button with proper styling
- `SwitchMaterial`: Toggle switch for theme selection
- `ConstraintLayout`: Modern layout management
- Material 3 Typography: Headline, Title, and Body text styles

## Architecture

### MainActivity.kt
The main activity handles:
- Setting the content view with the layout
- Managing theme switching logic
- Detecting current theme mode
- Responding to user interactions

Key code:
```kotlin
themeSwitch.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
```

### Layout Structure
The main layout (`activity_main.xml`) contains:
1. **Header Card**: Displays app title and subtitle
2. **Content Card**: Shows app description and action button
3. **Theme Card**: Contains the dark mode toggle switch

All cards use Material 3 styling with 16dp corner radius and proper elevation.

### Theme Files

#### Light Theme (`values/themes.xml`)
- Parent: `Theme.Material3.Light.NoActionBar`
- Primary color: Purple (#6750A4)
- Background: Near white (#FFFBFE)
- Surface: White with proper on-surface colors

#### Dark Theme (`values-night/themes.xml`)
- Parent: `Theme.Material3.Dark.NoActionBar`
- Primary color: Light purple (#D0BCFF)
- Background: Dark (#1C1B1F)
- Surface: Dark with proper contrast colors

### Color System
The app includes a complete Material 3 color palette in `colors.xml`:
- Primary, Secondary, and Tertiary color roles
- Container colors for backgrounds
- On-colors for text and icons
- Error colors for validation
- Inverse colors for special surfaces
- Outline colors for borders

## Dependencies

```gradle
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
```

## Build Configuration

- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin Version**: 1.9.0
- **Gradle Plugin**: 8.1.0
- **Java Version**: 21

## How to Build

1. Ensure you have Android Studio installed
2. Open the project in Android Studio
3. Wait for Gradle sync to complete
4. Click Run or use: `./gradlew assembleDebug`

## Project Files

```
app/
├── src/main/
│   ├── java/com/thertxnetwork/terminal/
│   │   └── MainActivity.kt          # Main activity with theme logic
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # Main UI layout
│   │   ├── values/
│   │   │   ├── colors.xml           # Material 3 color palette
│   │   │   ├── strings.xml          # App strings
│   │   │   └── themes.xml           # Light theme
│   │   ├── values-night/
│   │   │   └── themes.xml           # Dark theme
│   │   └── drawable/
│   │       └── ic_launcher_foreground.xml
│   └── AndroidManifest.xml          # App manifest
└── build.gradle                      # Module build config
```

## Future Enhancements

Potential improvements for this basic app:
1. Add more screens and navigation
2. Implement data persistence for theme preference
3. Add more Material Design 3 components (Chips, FAB, etc.)
4. Implement animations and transitions
5. Add responsive layouts for tablets
6. Include unit and UI tests

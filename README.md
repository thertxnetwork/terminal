# Terminal Android App

A simple, basic Android application built with Kotlin and Material Design 3, featuring dynamic color theming with both dark and light mode support.

## Features

- **Material Design 3**: Modern UI components following the latest Material Design guidelines
- **Dynamic Theming**: Support for both light and dark color themes
- **Theme Switcher**: Toggle between light and dark modes within the app
- **Kotlin**: Built entirely with Kotlin
- **Clean Architecture**: Simple, well-organized project structure

## Tech Stack

- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **UI Framework**: Material Design 3 (Material Components 1.11.0)
- **Build System**: Gradle

## Project Structure

```
terminal/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/terminal/app/
│   │       │   └── MainActivity.kt
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   └── values-night/
│   │       │       └── themes.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Building the Project

### Prerequisites

- Android Studio (latest version recommended)
- JDK 8 or higher
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

## Material Design 3 Implementation

The app implements Material Design 3 with:
- **Color System**: Complete Material 3 color palette for both themes
- **Typography**: Material 3 text styles (Headline, Title, Body)
- **Components**: MaterialCardView, MaterialButton, SwitchMaterial
- **Elevation & Shadows**: Proper elevation for depth
- **Corner Radius**: Rounded corners following MD3 guidelines

## Theme Support

The app includes two complete themes:
- **Light Theme**: Clean, bright interface with purple primary color
- **Dark Theme**: Easy on the eyes with reduced brightness

Users can switch between themes using the toggle switch in the app interface.

## License

This project is open source and available under the MIT License.
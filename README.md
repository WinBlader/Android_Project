# Taskify

Taskify is an Android app project built with Gradle and Android Studio. This repository contains the full Android Studio project so you can open, build, and run it locally.

## Requirements
- Android Studio (latest stable recommended)
- Android SDK and platform tools
- JDK 17 (or the version configured in `gradle.properties`)

## Getting Started
1. Clone the repository:
   ```bash
   git clone https://github.com/WinBlader/Android_Project.git
   cd Android_Project
   ```
2. Open the project in Android Studio.
3. Let Gradle sync finish.
4. Build the app:
   ```bash
   ./gradlew assembleDebug
   ```
5. Run on a device/emulator from Android Studio.

## Project Structure
- `app/src/main/java/` — Application source code
- `app/src/main/res/` — Resources (layouts, drawables, values)
- `app/build.gradle.kts` — App module build configuration
- `build.gradle.kts` — Root build configuration
- `gradle/` — Gradle wrapper files

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

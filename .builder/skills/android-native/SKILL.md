---
name: android-native
description: >
  Build and run the Android app on an emulator or device. Use when the user asks
  to build, compile, run, launch, or deploy the Android app, or mentions the APK,
  Gradle, emulator, or native-run, even if they don't explicitly say "android-native".
---

# Android Native

## Build

Run `./gradlew assembleDebug` to build the debug APK.

## Running on Emulator (preferred)

Use `npx native-run` to run the emulator on an Android device. Call `npx native-run android --list` to identify available devices. If there is more than one option, ask the user which one to use.

Example:
```bash
./gradlew assembleDebug && npx native-run android --app app/build/outputs/apk/debug/app-debug.apk --target Pixel_10_Pro
```

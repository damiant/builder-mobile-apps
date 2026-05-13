---
name: react-native
description: >
  Build and run a React Native (Expo) project on iOS simulator, Android emulator,
  or web. Use when the user asks to build, compile, run, launch, start, or preview
  the React Native or Expo app, or mentions expo, metro, simulator, emulator,
  or the react-native-app folder, even if they don't explicitly say "react-native".
---

# React Native (Expo)

The example project lives in `react-native-app/`. Always `cd` into the project directory before running commands.

## Install Dependencies

```bash
cd react-native-app && npm install
```

## Start the Dev Server

```bash
cd react-native-app && npx expo start
```

This opens the Expo CLI menu. From there, press:
- `i` — open in iOS simulator
- `a` — open in Android emulator
- `w` — open in web browser

## Run on a Specific Platform Directly

```bash
cd react-native-app && npx expo start --ios
cd react-native-app && npx expo start --android
cd react-native-app && npx expo start --web
```

## Build a Production Bundle (EAS / local)

For a local export (static bundle):

```bash
cd react-native-app && npx expo export
```

For a full native build via EAS (requires Expo account):

```bash
cd react-native-app && npx eas build --platform ios
cd react-native-app && npx eas build --platform android
```

## Gotchas

- Always `cd react-native-app` first — commands must run from the project root where `package.json` lives.
- The project uses Expo Router (`expo-router/entry` as main). Don't invoke `react-native run-ios` or `react-native run-android` directly — use `npx expo start` instead.
- If Metro bundler fails to start, clear the cache: `npx expo start --clear`
- iOS simulator requires Xcode and the iOS Simulator app installed. If unavailable, fall back to `--web`.
- Android emulator requires Android Studio and a running AVD. Use `npx native-run android --list` to check available targets.
- If dependencies are missing or mismatched, run `npm install` then retry.
- Write all commands on a single line — no backslash line continuations.

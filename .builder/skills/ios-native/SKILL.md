---
name: ios-native
description: >
  Set up, verify, build, and run the iOS app on a simulator or device. Use when
  the user invokes /ios-native install, asks to install or configure Xcode,
  or asks to build, compile, run, launch, or deploy the iOS app; also use when
  they mention xcodebuild, xcrun, an IPA, a simulator, or native-run.
---

# iOS Native

## Install and verify (`/ios-native install`)

Treat `install` as a setup and readiness check, not a request to build the app. Run these checks on the machine executing the commands, not on an assumed user Mac:

```bash
uname -s
command -v xcodebuild
xcode-select -p
xcodebuild -version
xcodebuild -showsdks
xcrun simctl list runtimes
xcrun simctl list devices available
command -v node
command -v npx
```

Check for the Xcode GUI and Simulator GUI independently with `test -d /Applications/Xcode.app` and `test -d /Applications/Xcode.app/Contents/Developer/Applications/Simulator.app`; Xcode command-line tools working does not mean Simulator.app is present. Locate the project's `.xcodeproj` or `.xcworkspace`, run `xcodebuild -list -project <Project>.xcodeproj` (or `-workspace`), then compare its deployment target with an available simulator runtime. Finally run `npx native-run ios --list` if `npx` is available; warn before proceeding if `npx` proposes installing a package. Do not guess a scheme or target ID.

If any check fails, give the user only the relevant next steps and repeat the failed check after they complete them:

- macOS is required for Xcode and iOS Simulator. On non-macOS hosts, explain that installation cannot be done there and guide the user to a Mac.
- If full Xcode is missing, direct the user to install **Xcode** through the Mac App Store, open it once to complete first-launch components and license prompts, and select its Command Line Tools in Xcode Settings → Locations. `xcode-select --install` installs only Command Line Tools, not the full Xcode app or Simulator.
- If the active developer directory points to Command Line Tools or the wrong Xcode, explain the mismatch. Ask before running `sudo xcode-select -s /Applications/Xcode.app/Contents/Developer` or any other system-wide/elevated change; otherwise guide the user through selecting Xcode in its Settings → Locations.
- If the target iOS runtime is missing or older than the app's deployment target, guide the user to install a compatible iOS simulator runtime from Xcode Settings → Platforms (or Components, depending on Xcode version), then select/create a simulator in Xcode's Devices and Simulators window.
- If `Simulator.app` is missing even though `xcodebuild` works, do not claim that `open -a Simulator` or `native-run` can show a window. Explain that the Xcode installation needs its Simulator component repaired/reinstalled through Xcode or the Mac App Store. `simctl` may still run an app headlessly, but cannot make its screen visible to the user here.
- If Node.js/`npx` is missing, guide the user to install Node.js using their preferred package manager or the official Node.js installer, then rerun the checks. Do not silently install packages or use an unapproved installer.

When prerequisites pass, verify the actual project with the Build and Running on Simulator steps below: build for a compatible simulator destination, find the resulting `.app`, then launch it. Ask which device to use if several are available. If the Simulator GUI is unavailable, `simctl boot`, `simctl install`, and `simctl launch` can verify a headless run instead, but do not describe it as a visible launch. Report any command that could not be verified rather than claiming the setup is complete.

Finish with a concise pass/fail summary for Xcode, project scheme, compatible runtime, build, Simulator GUI, and launch, stating whether GUI launch or only headless `simctl` is possible. A remote Builder browser preview does not display a native macOS Simulator window; opening Xcode on a remote host does not make it visible to the user.

## Build

Use `xcodebuild` to build the app. First, identify the scheme and workspace/project:

```bash
# List available schemes
xcodebuild -list
```

Build for simulator:

```bash
xcodebuild -scheme <SchemeName> -sdk iphonesimulator -configuration Debug -derivedDataPath build build
```

## Running on Simulator (preferred)

Use `npx native-run` to launch the app on an iOS simulator. First list available targets:

```bash
npx native-run ios --list
```

If there is more than one option, ask the user which one to use.

Then build and run:

```bash
xcodebuild -scheme <SchemeName> -sdk iphonesimulator -configuration Debug -derivedDataPath build build && npx native-run ios --app build/Build/Products/Debug-iphonesimulator/<AppName>.app --target <SimulatorUDID>
```

## Running on a Physical Device

Use `xcodebuild` with `-sdk iphoneos` and deploy via `xcrun`:

```bash
xcodebuild -scheme <SchemeName> -sdk iphoneos -configuration Debug -derivedDataPath build build
xcrun devicectl device install app --device <DeviceUDID> build/Build/Products/Debug-iphoneos/<AppName>.app
```

## Finding the App Bundle Path

After a successful build, the `.app` bundle is located at:

```
build/Build/Products/Debug-iphonesimulator/<AppName>.app   # simulator
build/Build/Products/Debug-iphoneos/<AppName>.app           # device
```

## Gotchas

- Always run `xcodebuild -list` first to get the correct scheme name — do not guess it.
- If the project uses a `.xcworkspace` (e.g. CocoaPods), pass `-workspace <Name>.xcworkspace` instead of `-project`.
- Use `npx native-run ios --list` to get the exact simulator UDID for the `--target` flag.
- Code signing is not required for simulator builds; for device builds, a valid provisioning profile and team ID are needed. If signing fails, inform the user.
- If `xcodebuild` is not found, use the install workflow above; Command Line Tools alone are insufficient for an iOS simulator build.

## Shell command formatting

Always write commands on a single line — no backslash line continuations. The command ACL uses glob patterns without dotAll, so embedded newlines break matching for commands like `xcodebuild *`. For long output, pipe to `tail`/`head` or write to a file.

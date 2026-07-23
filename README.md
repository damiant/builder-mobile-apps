<img width="1510" height="856" alt="Screenshot 2026-07-23 at 4 32 01 PM" src="https://github.com/user-attachments/assets/89cd8adb-afd9-4a7d-b53d-a1d1af83fd57" />

# Sample Mobile Projects

This is a demonstration of iOS and Android projects that were built using a Builder Prototype project. The prototype project was vibe coded in Builder and the handoff feature was used to implement these native projects.

## Prerequisites
- Xcode and Android Studio installed
- Node installed (optional if using the desktop app)

## Launching Builder
For Mobile apps there are 2 routes you can take:
- Use the [Builder Extension](https://marketplace.visualstudio.com/items?itemName=Builder.Builder) in your IDE (Cursor, VS Code etc)
- Use the [Builder Desktop App](https://www.builder.io/desktop-app) by creating a `Dockerless` Project.

Note: If you prefer CLI run `npx @builder.io/dev-tools@latest launch --app --chat`

## How?
- A blank iOS app was created using Xcode.
- A blank Android app was created using Android Studio.
- A blank React Native app was created using `npx create-expo-app@latest react-native-app --yes`
- Use the Builder VS Code extension (`Code` tab) or run the desktop app `npx @builder.io/dev-tools@latest launch --app --chat`

### Install Skills
Follow this to install skills for Android, iOS and Mobile Testing:
- Ask builder to `npx skills add BuilderIO/builder-agent-skills --skill android-native`
- Ask builder to `npx skills add BuilderIO/builder-agent-skills --skill ios-native`
- Ask builder to `npx skills add BuilderIO/builder-agent-skills --skill mobile-testing`

### Import a Prototype
In your Builder prototype copy the URL:
![alt text](image.png)

Then in your local IDE ask Builder:
`Import the prototype {url}`

You could also choose to bring in just the components or screens you need:
`Import the movie rating card component from the prototype {url}`

### Build the Apps


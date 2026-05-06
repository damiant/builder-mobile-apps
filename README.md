# Sample Mobile Projects

This is a demonstration of iOS and Android projects that were built using a Builder Prototype project. The prototype project was vibe coded in Builder and the handoff feature was used to implement these native projects.

## Prerequisites
- Xcode and Android Studio installed
- Node installed

## How?
- A blank iOS app was created using Xcode.
- A blank Android app was created using Android Studio.
- The Builder VS Code extension was installed and `Code` tab was opened
- Ask builder to `Run npx builder-doctor install-skill android-native`
- Ask builder to `Run npx builder-doctor install-skill ios-native`

### Import the Prototype
In your Builder prototype copy the URL:
![alt text](image.png)

Then in your local IDE ask Builder:
`Import the prototype {url}`

You could also choose to bring in just the components or screens you need:
`Import the movie rating card component from the prototype {url}`
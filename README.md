# Milhouse

A D&D 5e companion app for Android. Milhouse helps you manage the details for one or more characters — spells, abilities, actions, and notes — all stored locally on your device.

## Features

- **Multiple characters** — create characters with a name, class, species, and a unique color and icon
- **Spells** — track your spell list with full details (school, level, components, casting time, concentration, ritual), filter by level and class, and import spells from the D&D 5e SRD via [dnd5eapi.co](https://www.dnd5eapi.co)
- **Abilities** — track class features, species traits, feats, and other abilities with use tracking and recharge conditions
- **Actions** — store attack and action details including damage dice, to-hit bonus, range, and saving throws
- **Notes** — freeform notes with tags and full-text search
- **Species autocomplete** — species field suggests from a built-in list (PHB + supplemental) and auto-imports species traits as abilities on character creation

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM
- **Database:** Room (local, no backend)
- **Build:** AGP 9.1, KSP 2, Kotlin 2.2
- **Min SDK:** 29 (Android 10)

## Running the App

### Prerequisites

- Android Studio Meerkat (2024.3) or newer
- Android SDK 29+
- A physical device or emulator running Android 10+

### Steps

1. Clone the repo:
   ```
   git clone https://github.com/<your-username>/MilhouseApp.git
   ```
2. Open the project in Android Studio (`File > Open` → select the `MilhouseApp` folder)
3. Let Gradle sync complete
4. Run on a device or emulator via the **Run** button or:
   ```
   ./gradlew installDebug
   ```

No API keys or external accounts are required. The SRD spell import feature fetches from the free public API at `dnd5eapi.co` — no authentication needed.

## Notes

- All data is stored locally using Room. Uninstalling the app will delete all character data.
- The app does not sync across devices.

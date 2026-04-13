# iOS / Kotlin Multiplatform Migration Plan

## Overview
Migrate MilhouseApp from Android-only to Kotlin Multiplatform (KMP) + Compose Multiplatform,
sharing all business logic and UI between Android and iOS. Estimated effort: 12–18 hours.

---

## Target Project Structure

```
MilhouseApp/
├── shared/                        ← NEW: models, DAOs, repos, ViewModels, static data
│   ├── commonMain/
│   ├── androidMain/               ← Android SQLDelight driver
│   └── iosMain/                   ← iOS SQLDelight driver
├── androidApp/                    ← current app, mostly unchanged
│   └── MainActivity.kt, theme, mipmap assets...
└── iosApp/                        ← NEW: Xcode project (entry point only)
```

---

## What Stays the Same (Already KMP-Compatible)
- All Compose UI screens (10 screens)
- Coroutines + Flow + StateFlow
- Material 3 icons
- All data models (DndCharacter, Spell, Ability, DndAction, Note)
- All static data files (ClassFeatures, SpeciesData, StandardActions)
- All repository interfaces and logic
- All ViewModel business logic

---

## Required Library Replacements

| Current | KMP Replacement | Affected Files |
|---|---|---|
| **Room** | **SQLDelight** | AppDatabase.kt, all 5 DAOs, MilhouseApplication.kt |
| **HttpURLConnection + org.json** | **Ktor Client + kotlinx.serialization** | SpellImportService.kt (1 file) |
| **androidx.navigation** | **Navigation Compose Multiplatform** (JetBrains) | AppNavigation.kt |
| **LocalContext cast pattern** | **Koin** (KMP DI) | 9 UI screens + AppNavigation.kt |
| **viewModelScope** | **lifecycle-viewmodel KMP** | All 5 ViewModels |

---

## Migration Steps

### Phase 1 — Restructure Gradle (~2–4 hours)
- Convert project to multiplatform with `shared`, `androidApp`, `iosApp` modules
- Update root `build.gradle.kts` and `settings.gradle.kts`
- Add KMP targets: `androidTarget()`, `iosArm64()`, `iosSimulatorArm64()`
- Add new dependencies to version catalog:
  - `app.cash.sqldelight` (SQLDelight)
  - `io.ktor:ktor-client-core` + `ktor-client-darwin` (iOS) + `ktor-client-okhttp` (Android)
  - `org.jetbrains.kotlinx:kotlinx-serialization-json`
  - `io.insert-koin:koin-compose-multiplatform`
  - `androidx.navigation:navigation-compose` (JetBrains multiplatform version)
  - `androidx.lifecycle:lifecycle-viewmodel` (KMP version)

### Phase 2 — Database: Room → SQLDelight (~4–6 hours)
- Create `.sq` files for each entity (replaces DAOs + annotations):
  - `Character.sq`, `Spell.sq`, `Ability.sq`, `Action.sq`, `Note.sq`
- Replace `@Entity`, `@Dao`, `@Database` annotations with SQLDelight generated interfaces
- Replace `Room.databaseBuilder()` with SQLDelight `DatabaseDriverFactory` expect/actual:
  ```kotlin
  // commonMain
  expect class DatabaseDriverFactory { fun createDriver(): SqlDriver }
  // androidMain
  actual class DatabaseDriverFactory(private val context: Context) { ... }
  // iosMain
  actual class DatabaseDriverFactory { ... }
  ```
- Rewrite `MIGRATION_1_2` as SQLDelight migration file
- Keep `Flow<List<T>>` return types — SQLDelight supports these natively

### Phase 3 — Dependency Injection: LocalContext → Koin (~2–3 hours)
- Add Koin multiplatform module in `commonMain`:
  ```kotlin
  val appModule = module {
      single { DatabaseDriverFactory() }
      single { CharacterRepository(get()) }
      single { SpellRepository(get()) }
      // ... etc
  }
  ```
- Replace all `(LocalContext.current.applicationContext as MilhouseApplication).xRepository`
  with `val repo: XRepository by inject()` (9 screens + AppNavigation)
- Initialize Koin in Android `Application` and iOS entry point

### Phase 4 — Networking: HttpURLConnection → Ktor (~1–2 hours)
- Replace `SpellImportService.kt` HTTP logic:
  ```kotlin
  // before
  val conn = URL(url).openConnection() as HttpURLConnection
  conn.inputStream.bufferedReader().readText()

  // after
  val client = HttpClient()
  client.get(url).bodyAsText()
  ```
- Replace `org.json.JSONObject` parsing with `@Serializable` data classes +
  `Json.decodeFromString()`

### Phase 5 — Navigation (~1 hour)
- Swap `androidx.navigation` imports for JetBrains multiplatform equivalents
- API is nearly identical — mostly an import change

### Phase 6 — iOS Entry Point + Xcode Setup (~1–2 hours)
- Create `iosApp/` Xcode project
- Write Swift entry point:
  ```swift
  @main
  struct iOSApp: App {
      var body: some Scene {
          WindowGroup {
              ContentView()
          }
      }
  }
  ```
- Wire Compose Multiplatform root composable into SwiftUI via `ComposeUIViewController`
- Initialize Koin for iOS

---

## Platform-Specific Notes

### App Icon
- The `PackageManager.getApplicationIcon()` trick used in `CharacterListScreen.kt` is
  Android-only. On iOS, use `painterResource(Res.drawable.ic_launcher)` via
  Compose Multiplatform resources instead.

### Easter Egg Photo
- `R.drawable.milhouse_photo` needs to move to the shared `composeResources/` folder
  and be referenced via `painterResource(Res.drawable.milhouse_photo)`.

### Edge-to-Edge
- `enableEdgeToEdge()` in `MainActivity.kt` is Android-only. iOS handles safe areas
  natively through Compose Multiplatform — no equivalent call needed.

### Timestamps
- `System.currentTimeMillis()` in `Note.kt` works on both platforms but consider
  migrating to `kotlinx-datetime` for consistency.

---

## Dependency Versions (at time of writing)
- Kotlin: `2.2.10`
- KSP: `2.2.10-2.0.2`
- SQLDelight: `2.0.2`
- Ktor: `3.1.x`
- kotlinx.serialization: `1.7.x`
- Koin: `4.x`
- Compose Multiplatform: `1.7.x`
- Navigation Compose (JetBrains): `2.8.x`

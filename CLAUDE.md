# Project Stack
- Kotlin only
- Jetpack Compose (no XML)
- MVVM architecture
- Manual DI via `ViewModel.factory(...)` — no Hilt
- Room for local persistence
- Navigation Compose for routing
- Coroutines + Flow (no LiveData)

# Rules
- No business logic in composables
- ViewModels handle state
- No XML layouts

# AGP 9 Build Constraints
This project uses AGP 9.1.0 with built-in Kotlin support. This has important implications:

**Do NOT apply these plugins — they are incompatible with AGP 9 built-in Kotlin:**
- `org.jetbrains.kotlin.android` — AGP 9 handles Kotlin compilation natively
- `org.jetbrains.kotlin.kapt` — explicitly blocked by AGP 9

**Annotation processing:**
- Use `ksp(...)` dependency configuration for Room compiler — do NOT use `kapt(...)`

**KSP:**
- AGP 9 does NOT bundle KSP — the external `com.google.devtools.ksp` plugin IS required
- Apply it in both the root `build.gradle.kts` (`apply false`) and `app/build.gradle.kts`
- KSP2 changed the versioning scheme: it is now `<kotlin-version>-<ksp2-version>`, e.g. `2.2.10-2.0.2`
- The old `<kotlin-version>-1.0.<build>` scheme no longer applies for Kotlin 2.2+
- KSP generates sources via `kotlin.sourceSets` which AGP 9 warns about; suppressed via `android.disallowKotlinSourceSets=false` in `gradle.properties`

**Room schema export:**
- Keep `exportSchema = false` in `@Database` annotations
# Project Stack
- Kotlin Multiplatform (Android + iOS)
- Compose Multiplatform for shared UI
- MVVM architecture with Koin DI
- SQLDelight for local persistence
- Navigation Compose (JetBrains KMP fork) for routing
- Coroutines + Flow (no LiveData)

# Rules
- No business logic in composables
- ViewModels handle state
- No XML layouts
- All UI lives in `shared/commonMain`

# AGP 9 Build Constraints
This project uses AGP 9.1.0. **`android.builtInKotlin=false` is set in `shared/gradle.properties`** (scoped to `:shared` only), which allows `com.android.library` + `kotlin.multiplatform` to coexist there without AGP's incompatibility check.

**Plugin rules:**
- `:shared` uses `com.android.library` + `kotlin.multiplatform` — standard KMP setup
- `:app` uses AGP's built-in Kotlin (do NOT apply `org.jetbrains.kotlin.android` — it uses the removed `BaseExtension` API and fails with AGP 9.1.0)
- Do NOT apply `org.jetbrains.kotlin.kapt` — explicitly blocked by AGP 9; use `ksp(...)` instead
- Do NOT apply `com.android.kotlin.multiplatform.library` — Compose Resources plugin does not integrate with it

**Why both `android.builtInKotlin=false` and `android.newDsl=false` are required (root `gradle.properties`):**
- `com.android.kotlin.multiplatform.library` (AGP's native KMP plugin) does not wire Compose Resources into Android asset packaging
- The standard `com.android.library` + `kotlin.multiplatform` combo does wire correctly, but requires `android.builtInKotlin=false` to bypass AGP 9's incompatibility check
- `android.builtInKotlin=false` alone breaks `:app`: `kotlin.android` v2.2.10 in "full Kotlin" mode uses the removed `BaseExtension` API → ClassCastException at sync
- `android.newDsl=false` restores the old extension hierarchy (including `BaseExtension`), letting `kotlin.android` apply cleanly
- AGP reads both properties globally from the root project — they cannot be scoped to individual subprojects

**JVM target alignment in `:app`:**
- `compileOptions` sets Java to 11; `kotlinOptions { jvmTarget = "11" }` must also be set or Kotlin defaults to 21 → build failure

**KSP:**
- AGP 9 does NOT bundle KSP — the external `com.google.devtools.ksp` plugin IS required if used
- KSP2 versioning: `<kotlin-version>-<ksp2-version>`, e.g. `2.2.10-2.0.2`
- KSP generates sources via `kotlin.sourceSets`; suppressed via `android.disallowKotlinSourceSets=false` in root `gradle.properties`

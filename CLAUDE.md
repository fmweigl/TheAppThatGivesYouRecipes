# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin Multiplatform + Compose Multiplatform app targeting Android, iOS (arm64 + simulator arm64), and Desktop (JVM). Early stage: the only feature is `randomrecipe`, which loads a random meal from TheMealDB.

## Commands

- Android debug build: `./gradlew :androidApp:assembleDebug`
- Desktop run: `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`)
- iOS: open `iosApp/` in Xcode and run from there (the Xcode build phase runs `:composeApp:embedAndSignAppleFrameworkForXcode`, which produces the static `Shared` framework)
- Tests:
  - JVM tests per module: `./gradlew :randomrecipe:domain:jvmTest` (also `:randomrecipe:data`, `:randomrecipe:ui`)
  - iOS simulator tests: `./gradlew :randomrecipe:domain:iosSimulatorArm64Test`
  - Single test: `./gradlew :randomrecipe:domain:jvmTest --tests "com.example.yetanothermealsapp.randomrecipe.domain.SomeTest.someMethod"`
  - Tests exist in `:randomrecipe:data` (repository + mapping, via Ktor `MockEngine`) and `:randomrecipe:ui` (ViewModel).

No lint/format tooling (ktlint, detekt, spotless) is configured.

## Architecture

- `:composeApp` is the UI entry point for all platforms and will hold the main navigation. It owns the root composable `App()` (`com.example.yetanothermealsapp`), depends on the feature `ui` and `data` modules, defines `initKoin()` (`di/Koin.kt`) listing each feature's Koin modules, and produces the static iOS `Shared` framework. Each platform entry point calls `initKoin()` once at startup, then hosts `App()`:
  - `androidApp/`: `MealsApplication.onCreate()` calls `initKoin { androidContext(...) }`; `MainActivity` calls `setContent { App() }`. A plain `com.android.application` module (not KMP) that depends on `:composeApp`.
  - `desktopApp/`: `main()` calls `initKoin()`, then opens a Compose `Window` with `App()`. A `kotlin("jvm")` module (not KMP) using `compose.desktop`, depends on `:composeApp`.
  - `iosApp/`: `iOSApp.init()` calls `KoinKt.doInitKoin(config: nil)` (Kotlin `initKoin` is exported with a `do` prefix); SwiftUI wraps `MainViewController()` from `composeApp/src/iosMain`, imported in Swift as `import Shared`
- `:composeApp` and `:randomrecipe:ui` use the newer AGP `com.android.kotlin.multiplatform.library` plugin: Android config lives inside `kotlin { android { ... } }`, not a top-level `android {}` block.
- `randomrecipe/` is a feature folder with three modules, referenced through type-safe project accessors (`projects.randomrecipe.domain`):
  - `:randomrecipe:domain`: models and repository interfaces. KMP (jvm + iOS), with no Android or Compose dependencies.
  - `:randomrecipe:data`: repository implementations, depends on `domain`. Its only public declaration is the Koin module `randomRecipeDataModule`; everything else is `internal`. Same targets and constraints as `domain`. Uses Ktor + kotlinx.serialization (OkHttp engine on `jvm`, Darwin on iOS); the internal `createTheMealDbHttpClient()` builds the configured client. Meals are decoded as raw `JsonObject`s and mapped in `MealMapper.kt` because of the numbered `strIngredientN`/`strMeasureN` fields.
  - `:randomrecipe:ui`: Compose Multiplatform screens (Android, jvm, iOS), depends on `domain` (`implementation`: consumers never need domain types from it). Its only public declarations are `RandomRecipeEntry` (the composable `:composeApp` calls) and the Koin module `randomRecipeUiModule`; everything else is `internal`. `RandomRecipeEntry` calls `RandomRecipeRoute`, which gets `RandomRecipeViewModel` via `koinViewModel()`, collects its `RandomRecipeUiState` `StateFlow`, and passes state and callbacks to the stateless `RandomRecipeScreen`. Follow this Entry (public) / Route (stateful) / Screen (stateless) layering and keep visibility as tight as possible in new features; Entry is where navigation registration will go once navigation is added. Images load with Coil 3 (`coil-network-ktor3`). Consumed by `:composeApp`; it does not build an iOS framework itself.
  - `domain` and `data` are KMP rather than `kotlin("jvm")` so iOS can consume them. Their Android consumers resolve the `jvm()` variant.
- Platform-specific code goes in `expect`/`actual` declarations.
- Dependency injection uses Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`). Each feature layer exposes its own Koin module in a `di` package (e.g. `randomRecipeDataModule` binds the `HttpClient` and repository, `randomRecipeUiModule` declares ViewModels with `viewModelOf`). `:composeApp`'s `initKoin()` registers them via `startKoin`; Koin is not started inside Compose. Screens obtain ViewModels with `koinViewModel()`, which uses the globally started Koin. Add new feature modules to the list in `initKoin()`.
- Lifecycle ViewModel and runtime-compose (JetBrains multiplatform artifacts) are already available in `:randomrecipe:ui` `commonMain`.
- Dependencies and versions are managed in `gradle/libs.versions.toml`, which uses bleeding-edge versions (AGP 9.x, Kotlin 2.4.x, compileSdk 37). JVM target is 11. Package/namespace: `com.example.yetanothermealsapp`.

## Data source

Recipes come from TheMealDB JSON API. The API reference is in `docs/themealdb-api.md`, and the project skill `.claude/skills/themealdb` covers using it.

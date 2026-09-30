# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin Multiplatform + Compose Multiplatform app targeting Android, iOS (arm64 + simulator arm64), and Desktop (JVM). Early stage: the feature modules contain placeholder code.

## Commands

- Android debug build: `./gradlew :androidApp:assembleDebug`
- Desktop run: `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`)
- iOS: open `iosApp/` in Xcode and run from there (the Xcode build phase runs `:randomrecipe:ui:embedAndSignAppleFrameworkForXcode`, which produces the static `Shared` framework)
- Tests:
  - JVM tests per module: `./gradlew :randomrecipe:domain:jvmTest` (also `:randomrecipe:data`, `:randomrecipe:ui`)
  - iOS simulator tests: `./gradlew :randomrecipe:domain:iosSimulatorArm64Test`
  - Single test: `./gradlew :randomrecipe:domain:jvmTest --tests "com.example.yetanothermealsapp.randomrecipe.domain.SomeTest.someMethod"`
  - No tests exist yet.

No lint/format tooling (ktlint, detekt, spotless) is configured.

## Architecture

- There is no `shared` module. The root composable `App()` lives in `:randomrecipe:ui` (`com.example.yetanothermealsapp.randomrecipe.ui`), and each platform entry point just hosts it:
  - `androidApp/`: `MainActivity` calls `setContent { App() }`
  - `desktopApp/`: `main.kt` opens a Compose `Window` with `App()`
  - `iosApp/`: SwiftUI wraps `MainViewController()` from `randomrecipe/ui/src/iosMain`, imported in Swift as `import Shared`
- `:randomrecipe:ui` uses the newer AGP `com.android.kotlin.multiplatform.library` plugin: Android config lives inside `kotlin { android { ... } }`, not a top-level `android {}` block.
- `randomrecipe/` is a feature folder with three modules, referenced through type-safe project accessors (`projects.randomrecipe.domain`):
  - `:randomrecipe:domain`: models and repository interfaces. KMP (jvm + iOS), with no Android or Compose dependencies.
  - `:randomrecipe:data`: repository implementations, depends on `domain`. Same targets and constraints as `domain`.
  - `:randomrecipe:ui`: Compose Multiplatform screens (Android, jvm, iOS), depends on `domain`.
  - `domain` and `data` are KMP rather than `kotlin("jvm")` so iOS can consume them. Their Android consumers resolve the `jvm()` variant.
- Platform-specific code goes in `expect`/`actual` declarations.
- Lifecycle ViewModel and runtime-compose (JetBrains multiplatform artifacts) are already available in `:randomrecipe:ui` `commonMain`.
- Dependencies and versions are managed in `gradle/libs.versions.toml`, which uses bleeding-edge versions (AGP 9.x, Kotlin 2.4.x, compileSdk 37). JVM target is 11. Package/namespace: `com.example.yetanothermealsapp`.

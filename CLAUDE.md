# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin Multiplatform + Compose Multiplatform app targeting Android, iOS (arm64 + simulator arm64), and Desktop (JVM). Currently close to the stock JetBrains KMP wizard template (`App()` is a "Click me!" demo), so most of the app has yet to be written.

## Commands

- Android debug build: `./gradlew :androidApp:assembleDebug`
- Desktop run: `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`)
- iOS: open `iosApp/` in Xcode and run from there (links the static `Shared` framework)
- Tests:
  - Android host (unit) tests: `./gradlew :shared:testAndroidHostTest`
  - Desktop/JVM tests: `./gradlew :shared:jvmTest`
  - iOS simulator tests: `./gradlew :shared:iosSimulatorArm64Test`
  - Single test: `./gradlew :shared:jvmTest --tests "com.example.yetanothermealsapp.SharedLogicDesktopTest.example"`

No lint/format tooling (ktlint, detekt, spotless) is configured.

## Architecture

- `shared/` holds all app code and UI. The root composable is `App()` in `shared/src/commonMain/.../App.kt`, and each platform entry point just hosts it:
  - `androidApp/`: `MainActivity` calls `setContent { App() }`
  - `desktopApp/`: `main.kt` opens a Compose `Window` with `App()`
  - `iosApp/`: SwiftUI wraps `MainViewController()` from `shared/src/iosMain`
- `shared` uses the newer AGP `com.android.kotlin.multiplatform.library` plugin: Android config lives inside `kotlin { android { ... } }`, not a top-level `android {}` block. Its tests are split into `androidHostTest` (JVM unit tests) and a device test source set.
- Platform-specific code uses `expect`/`actual` (for example `getPlatform()` in `Platform.kt`, with `Platform.android.kt`, `Platform.ios.kt`, `Platform.jvm.kt`).
- Shared resources go through Compose Multiplatform resources, accessed via the generated `yetanothermealsapp.shared.generated.resources.Res`.
- Lifecycle ViewModel and runtime-compose (JetBrains multiplatform artifacts) are already available in `commonMain`.
- Dependencies and versions are managed in `gradle/libs.versions.toml`, which uses bleeding-edge versions (AGP 9.x, Kotlin 2.4.x, compileSdk 37). JVM target is 11. Package/namespace: `com.example.yetanothermealsapp`.

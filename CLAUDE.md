# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin Multiplatform + Compose Multiplatform app targeting Android, iOS (arm64 + simulator arm64), and Desktop (JVM). Early stage: a bottom navigation bar (Navigation 3) with two tabs: the `randomrecipe` feature, which loads a random meal from TheMealDB, and the `about` feature, which lists the app's open source libraries and their licenses.

## Commands

- Android debug build: `./gradlew :androidApp:assembleDebug`
- Desktop run: `./gradlew :desktopApp:run` (hot reload: `./gradlew :desktopApp:hotRun --auto`)
- iOS: open `iosApp/` in Xcode and run from there (the Xcode build phase runs `:composeApp:embedAndSignAppleFrameworkForXcode`, which produces the static `Shared` framework)
- Tests:
  - JVM tests per module: `./gradlew :feature:randomrecipe:domain:jvmTest` (also `:feature:randomrecipe:data`, `:feature:randomrecipe:ui`, `:core:domain`, `:core:data`, `:core:network`)
  - iOS simulator tests: `./gradlew :feature:randomrecipe:domain:iosSimulatorArm64Test`
  - Single test: `./gradlew :feature:randomrecipe:domain:jvmTest --tests "io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.SomeTest.someMethod"`
  - Tests exist in `:core:domain` (`Result`), `:core:data` (`safeApiCall` error mapping), `:core:network` (client config and Koin module), `:feature:randomrecipe:data` (repository + mapping, via Ktor `MockEngine`) and `:feature:randomrecipe:ui` (ViewModel).

Static analysis: detekt (`./gradlew detekt`, also part of `check`). The `meals.detekt` convention plugin applies it to every module with the shared config `config/detekt/detekt.yml` on top of detekt's defaults, and analyzes all source sets under `src/`. Fix findings rather than suppressing them; suppress only with an inline comment explaining why (see `SafeApiCall.kt`). Functions annotated `@Composable` are exempt from `FunctionNaming`. No formatter (ktlint, spotless) is configured.

## CI

GitHub Actions (`.github/workflows/ci.yml`) runs `./gradlew detekt jvmTest :androidApp:assembleDebug` on pushes and pull requests to `master`. Run this command locally and make sure it passes before a task counts as done. CI doesn't build or test iOS.

## Build logic

Shared Gradle setup lives in convention plugins in the included build `build-logic/` (wired in through `includeBuild("build-logic")` in `pluginManagement`). It reads the root version catalog. **Every new KMP module must apply exactly one of these plugins**; its own build file then only holds the plugin, the `namespace` (UI modules) and module-specific dependencies.

| Plugin | Use for | Provides |
|---|---|---|
| `meals.kmp.domain` | `domain` modules | KMP with the shared targets; kotlin-test in commonTest; `verifyDomainDependencies` (runs as part of `check`) |
| `meals.kmp.data` | `data` modules (also `:core:network`) | KMP with the shared targets + kotlinx-serialization; kotlin-test, kotlinx-coroutines-test, ktor-client-mock in commonTest |
| `meals.detekt` | every module (applied by the three KMP plugins; `:composeApp`, `:androidApp`, `:desktopApp` apply it directly) | detekt with the shared config, covering all source sets |
| `meals.kmp.feature.ui` | feature `ui` modules | KMP + `com.android.kotlin.multiplatform.library` + Compose MP + Compose compiler + kotlinx-serialization (for `NavKey`s); shared targets plus `android` (compileSdk/minSdk from the catalog, JVM target 11); Compose runtime/foundation/material3/ui, lifecycle viewmodel-compose + runtime-compose, koin-compose-viewmodel, navigation3-runtime in commonMain; kotlin-test, kotlinx-coroutines-test in commonTest |

- The target list (jvm, iosArm64, iosSimulatorArm64) exists once, in `sharedKmpTargets()` (`build-logic/convention/.../KmpTargets.kt`). The Android library defaults are in `androidLibraryDefaults()`. `:composeApp` calls both directly; the root `build.gradle.kts` loads the convention plugins with `apply false` so these helpers are importable from module build scripts.
- **Domain rule:** a domain module's commonMain must not depend on Ktor (`io.ktor`), Koin (`io.insert-koin`), AndroidX (`androidx`, `org.jetbrains.androidx`) or Compose (`org.jetbrains.compose`), directly or through another module. `verifyDomainDependencies` fails `check` and names the offending dependency.
- `build-logic` gets the Gradle plugins as `compileOnly` dependencies derived from the catalog's `[plugins]` entries; add new plugins there the same way.

## Architecture

- `:composeApp` is the UI entry point for all platforms and holds the main navigation. It owns the root composable `App()` (`io.github.fmweigl.yetanothermealsapp`), depends on the feature `ui` and `data` modules, defines `initKoin()` (`di/Koin.kt`) listing each feature's Koin modules, and produces the static iOS `Shared` framework. Each platform entry point calls `initKoin()` once at startup, then hosts `App()`:
  - `androidApp/`: `MealsApplication.onCreate()` calls `initKoin { androidContext(...) }`; `MainActivity` calls `setContent { App() }`. A plain `com.android.application` module (not KMP) that depends on `:composeApp`.
  - `desktopApp/`: `main()` calls `initKoin()`, then opens a Compose `Window` with `App()`. A `kotlin("jvm")` module (not KMP) using `compose.desktop`, depends on `:composeApp`.
  - `iosApp/`: `iOSApp.init()` calls `KoinKt.doInitKoin(config: nil)` (Kotlin `initKoin` is exported with a `do` prefix); SwiftUI wraps `MainViewController()` from `composeApp/src/iosMain`, imported in Swift as `import Shared`
- Navigation uses Navigation 3 (JetBrains multiplatform `navigation3-ui` and `lifecycle-viewmodel-navigation3`; the runtime is Google's multiplatform `androidx.navigation3:navigation3-runtime`, pinned to the version `navigation3-ui` requires). It lives in `:composeApp`'s `navigation` package, following the multiple back stacks recipe:
  - `AppNavigation` holds the `Scaffold` with the bottom `NavigationBar` and the `NavDisplay`. `topLevelDestinations` lists the tabs (the first is the start route); the `entryProvider` calls each feature's entry registration (`randomRecipeEntry()`).
  - `NavigationState` keeps the selected tab and one `NavBackStack` per tab, saved across configuration changes and process death. Each stack has its own saveable-state and ViewModel-store decorators, so ViewModels are scoped to their entry and a tab keeps its state while another is shown. `Navigator` changes it: `navigate()` switches tabs or pushes onto the current stack, `goBack()` pops, and from the root of another tab returns to the start tab ("exit through home").
  - Every `NavKey` must be `@Serializable` and registered in `navKeyConfiguration` (`AppNavigation.kt`): outside Android, back stacks can only be saved with explicitly registered subtypes.
- `:composeApp` and `:feature:randomrecipe:ui` use the newer AGP `com.android.kotlin.multiplatform.library` plugin: Android config lives inside `kotlin { android { ... } }`, not a top-level `android {}` block. In UI modules that block only sets `namespace`; the rest comes from `meals.kmp.feature.ui`.
- `feature/randomrecipe/` is a feature folder with three modules, referenced through type-safe project accessors (`projects.feature.randomrecipe.domain`):
  - `:feature:randomrecipe:domain`: models and repository interfaces. KMP (jvm + iOS), with no Android or Compose dependencies.
  - `:feature:randomrecipe:data`: repository implementations, depends on `domain`. Its only public declaration is the Koin module `randomRecipeDataModule`; everything else is `internal`. Same targets and constraints as `domain`. Gets its `HttpClient` from `:core:network` and decodes responses with kotlinx.serialization. Meals are decoded as raw `JsonObject`s and mapped in `MealMapper.kt` because of the numbered `strIngredientN`/`strMeasureN` fields.
  - `:feature:randomrecipe:ui`: Compose Multiplatform screens (Android, jvm, iOS), depends on `domain` (`implementation`: consumers never need domain types from it). Its only public declarations are the navigation key `RandomRecipeNavKey`, the entry registration `EntryProviderScope<NavKey>.randomRecipeEntry()` (both in `RandomRecipeNavKey.kt`, called from `:composeApp`'s navigation) and the Koin module `randomRecipeUiModule`; everything else is `internal`. `randomRecipeEntry()` maps `RandomRecipeNavKey` to `RandomRecipeRoute`, which gets `RandomRecipeViewModel` via `koinViewModel()`, collects its `RandomRecipeUiState` `StateFlow`, and passes state and callbacks to the stateless `RandomRecipeScreen`. Follow this NavKey + entry registration (public) / Route (stateful) / Screen (stateless) layering and keep visibility as tight as possible in new features. Images load with Coil 3 (`coil-network-ktor3`). Consumed by `:composeApp`; it does not build an iOS framework itself.
  - `domain` and `data` are KMP rather than `kotlin("jvm")` so iOS can consume them. Their Android consumers resolve the `jvm()` variant.
- `feature/about/` has only a `ui` module, `:feature:about:ui` (no domain or data yet). It shows the app's libraries and licenses with AboutLibraries' `LibrariesContainer` (`aboutlibraries-compose-m3`), exposed through `AboutNavKey` and `aboutEntry(loadLibrariesJson)`; `AboutRoute` loads the list with `produceLibraries`, `AboutScreen` displays it. It has no ViewModel and no Koin module. A feature only needs the layers it uses.
- The library list is generated by the AboutLibraries Gradle plugin in `:composeApp`, not in the about module: only `:composeApp` sees every dependency. `exportLibraryDefinitions` writes `build/generated/aboutLibrariesResources/files/aboutlibraries.json` on every build (fetching license texts from SPDX, so it needs network unless cached); that directory is `:composeApp`'s commonMain Compose resources directory (`compose.resources { customDirectory(...) }`, so a `src/commonMain/composeResources` folder would be ignored), and `AppNavigation` passes `{ Res.readBytes("files/aboutlibraries.json") }` to `aboutEntry`. `filterVariants` limits collection to what ships (`android`, `jvm`, `metadataIosMain`). `androidResources.enable = true` in `:composeApp` is required for Compose resources to reach the Android app. The plugin also adds an empty `res/raw/aboutlibraries.json` through its own Android hook; it is unused.
- `core/` holds code shared by all features (KMP, jvm + iOS, no Android or Compose dependencies):
  - `:core:domain`: the typed `Result<D, E : Error>` (with `map`/`flatMap`/`onSuccess`/`onFailure`), the `Error` marker interface, and `DataError` (`NoConnection`, `Timeout`, `Server`, `InvalidResponse`, `Unknown`). Feature `domain` modules expose it with `api(projects.core.domain)`.
  - `:core:network`: the app's single TheMealDB `HttpClient`: base URL, `expectSuccess = true`, JSON content negotiation, and the platform engines (OkHttp on `jvm`, Darwin on iOS). Its Koin module `coreNetworkModule` provides the client; features inject it and never create their own. The public `createTheMealDbHttpClient(engine)` exists for tests with a `MockEngine`.
  - `:core:data`: `safeApiCall { }`, which runs a Ktor request and maps its exceptions to `DataError` (rethrowing `CancellationException`). Relies on `expectSuccess = true`.
- Error handling: repositories return `Result<T, DataError>` and never throw; Ktor and serialization exceptions stay inside `data`. Wrap every request in `safeApiCall` and return `DataError.InvalidResponse` for responses that parse but can't be mapped (mappers return null rather than throwing). ViewModels put the `DataError` into their UI state, and `ui` turns it into a message (`DataError.toMessage()`).
- Platform-specific code goes in `expect`/`actual` declarations.
- Dependency injection uses Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`). Each feature layer exposes its own Koin module in a `di` package (e.g. `randomRecipeDataModule` binds the repository, `randomRecipeUiModule` declares ViewModels with `viewModelOf`). `:composeApp`'s `initKoin()` registers them, together with `coreNetworkModule`, via `startKoin`; Koin is not started inside Compose. Screens obtain ViewModels with `koinViewModel()`, which uses the globally started Koin. Add new feature modules to the list in `initKoin()`.
- Lifecycle ViewModel and runtime-compose (JetBrains multiplatform artifacts) are available in every feature `ui` module's `commonMain` through `meals.kmp.feature.ui`.
- Dependencies and versions are managed in `gradle/libs.versions.toml`, which uses bleeding-edge versions (AGP 9.x, Kotlin 2.4.x, compileSdk 37). JVM target is 11. Package/namespace: `io.github.fmweigl.yetanothermealsapp`.

## Data source

Recipes come from TheMealDB JSON API. The API reference is in `docs/themealdb-api.md`, and the project skill `.claude/skills/themealdb` covers using it.

[![CI](https://github.com/fmweigl/YetAnotherMealApp/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/fmweigl/YetAnotherMealApp/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)
![Kotlin](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Ffmweigl%2FYetAnotherMealApp%2Fmaster%2Fgradle%2Flibs.versions.toml&query=%24.versions.kotlin&label=Kotlin&logo=kotlin&color=7F52FF)
![Compose Multiplatform](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Ffmweigl%2FYetAnotherMealApp%2Fmaster%2Fgradle%2Flibs.versions.toml&query=%24.versions.composeMultiplatform&label=Compose%20Multiplatform&logo=jetpackcompose&color=4285F4)
![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS%20%7C%20Desktop-4C6B30)
![API](https://img.shields.io/badge/API-24%2B-A8401E?logo=android)
![No tracking](https://img.shields.io/badge/tracking-none-4C6B30)
[![Recipes by TheMealDB](https://img.shields.io/badge/recipes-TheMealDB-F2BF48)](https://www.themealdb.com)
![detekt](https://img.shields.io/badge/code%20style-detekt-orange)

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM).

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/composeApp](./composeApp) holds the root `App()` composable that the Android, desktop and iOS apps display.

* [/feature/recipe](./feature/recipe) is the recipe feature: random recipes, a recipe opened by id, and saving favorites:
  - [domain](./feature/recipe/domain) has the models and repository interfaces, plain Kotlin Multiplatform code with no Android or Compose dependencies.
  - [data](./feature/recipe/data) loads recipes from TheMealDB and stores favorites on the device in a [Room](https://developer.android.com/kotlin/multiplatform/room) database.
  - [ui](./feature/recipe/ui) has the feature's Compose Multiplatform screens, which `App()` displays.

* [/feature/favorites](./feature/favorites) is the favorites tab. Its [ui](./feature/favorites/ui) module lists the saved recipes; they open in the recipe feature's screen.

* [/feature/about](./feature/about) is the about feature. So far it only has a [ui](./feature/about/ui) module, which lists the app's libraries and their licenses (generated with [AboutLibraries](https://github.com/mikepenz/AboutLibraries)).

### Running the apps

Recipes come from [TheMealDB](https://www.themealdb.com)'s V2 API. Without further setup the app uses TheMealDB's public test key `1`, which is fine for development. To use a supporter key instead, put it in `local.properties` (not committed) as

```properties
theMealDbApiKey=YOUR_KEY
```

or provide it as the Gradle property `theMealDbApiKey` or the environment variable `THE_MEAL_DB_API_KEY` (e.g. a CI secret).

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- JVM tests: `./gradlew :feature:recipe:domain:jvmTest` (likewise for `data` and `ui`)
- iOS tests: `./gradlew :feature:recipe:domain:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## Attributions

Recipe data and images come from [TheMealDB](https://www.themealdb.com). See [attributions](./ATTRIBUTIONS.md).

## Privacy

The app collects no personal data. See the [privacy policy](./PRIVACY.md).

## License

This app is open source, licensed under the [Apache License 2.0](./LICENSE).

```
Copyright 2026 The YetAnotherMealsApp contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

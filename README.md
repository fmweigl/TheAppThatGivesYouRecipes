[![CI](https://github.com/fmweigl/YetAnotherMealApp/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/fmweigl/YetAnotherMealApp/actions/workflows/ci.yml)

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM).

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/composeApp](./composeApp) holds the root `App()` composable that the Android, desktop and iOS apps display.

* [/feature/randomrecipe](./feature/randomrecipe) is the random-recipe feature:
  - [domain](./feature/randomrecipe/domain) and [data](./feature/randomrecipe/data) contain plain Kotlin Multiplatform code with no Android or Compose dependencies.
  - [ui](./feature/randomrecipe/ui) has the feature's Compose Multiplatform screens, which `App()` displays.

* [/feature/about](./feature/about) is the about feature. So far it only has a [ui](./feature/about/ui) module, which lists the app's libraries and their licenses (generated with [AboutLibraries](https://github.com/mikepenz/AboutLibraries)).

### Running the apps

Recipes come from [TheMealDB](https://www.themealdb.com)'s V2 API. Without further setup the app uses TheMealDB's public test key `1`, which is fine for development. Release builds need a supporter key: put it in `local.properties` (not committed) as

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

- JVM tests: `./gradlew :feature:randomrecipe:domain:jvmTest` (likewise for `data` and `ui`)
- iOS tests: `./gradlew :feature:randomrecipe:domain:iosSimulatorArm64Test`

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

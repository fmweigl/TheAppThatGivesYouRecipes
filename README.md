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

* [/feature/randomrecipe](./feature/randomrecipe) is the random-recipe feature:
  - [domain](./feature/randomrecipe/domain) and [data](./feature/randomrecipe/data) contain plain Kotlin Multiplatform code with no Android or Compose dependencies.
  - [ui](./feature/randomrecipe/ui) has the feature's Compose Multiplatform screens, which `App()` displays.

* [/feature/about](./feature/about) is the about feature. So far it only has a [ui](./feature/about/ui) module, which lists the app's libraries and their licenses (generated with [AboutLibraries](https://github.com/mikepenz/AboutLibraries)).

### Running the apps

Recipes come from [TheMealDB](https://www.themealdb.com)'s V2 API. Without further setup the app uses TheMealDB's public test key `1`, which is fine for development. Release builds (Android `assembleRelease`/`bundleRelease`, desktop `packageRelease*`/`runRelease`, Xcode's Release configuration) need a supporter key and fail with the test key: put it in `local.properties` (not committed) as

```properties
theMealDbApiKey=YOUR_KEY
```

or provide it as the Gradle property `theMealDbApiKey` or the environment variable `THE_MEAL_DB_API_KEY` (e.g. a CI secret).

Android release builds are shrunk with R8 and signed with the Google Play upload key. Create the key once and keep it outside the repository (and backed up):

```shell
keytool -genkeypair -keystore ~/keys/yetanothermealsapp-upload.jks -alias upload -keyalg RSA -keysize 4096 -validity 10000
```

Then add to `local.properties`:

```properties
releaseStoreFile=/home/you/keys/yetanothermealsapp-upload.jks
releaseStorePassword=...
releaseKeyAlias=upload
releaseKeyPassword=...
```

(or the Gradle properties of the same names, or the environment variables `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS` and `RELEASE_KEY_PASSWORD`). A relative `releaseStoreFile` is resolved against the repository root. Without them, release builds fail. Build the bundle for Play with `./gradlew :androidApp:bundleRelease`.

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

## Publishing to Google Play

CI (`.github/workflows/ci.yml`, job `publish`) builds a signed release bundle on every push to `master` that passes detekt, the tests and the debug build, and uploads it to Google Play's **production** track, so it reaches all users once Play's review approves it. The version code is the GitHub run number, and the version name is `1.0.<run number>`. The R8 mapping file is uploaded too, so Play Console shows readable crash stack traces.

One-time setup:

1. In Play Console, create the app (package `io.github.fmweigl.yetanothermealsapp`), complete the store listing, content rating, data safety form and privacy policy URL, and upload the first bundle by hand (`./gradlew :androidApp:bundleRelease`); the Play API can't create an app. Enroll in Play App Signing with your upload key.
2. New personal developer accounts must run a closed test (at least 12 testers for 14 days) before Play grants production access; until then, uploads to production fail.
3. Create a service account in Google Cloud, enable the Google Play Android Developer API, and invite the service account in Play Console (Users and permissions) with permission to release to production for this app. Create a JSON key for it.
4. Add these repository secrets (Settings → Secrets and variables → Actions):
   - `PLAY_SERVICE_ACCOUNT_JSON`: the service account's JSON key
   - `RELEASE_KEYSTORE_BASE64`: the upload keystore, `base64 -w0 upload.jks`
   - `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`
   - `THE_MEAL_DB_API_KEY`: the supporter key
5. Add the repository variable `PLAY_PUBLISHING` with the value `true`. Without it the `publish` job is skipped, so CI stays green before the setup is done. Set it to anything else to pause publishing.

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

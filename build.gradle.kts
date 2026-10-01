plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    // Convention plugins from build-logic; loading them here also makes its helpers
    // (e.g. sharedKmpTargets()) available to every module's build script.
    id("meals.kmp.domain") apply false
    id("meals.kmp.data") apply false
    id("meals.kmp.feature.ui") apply false
}
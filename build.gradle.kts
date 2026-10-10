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
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.aboutLibraries) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room3) apply false
    // Convention plugins from build-logic; loading them here also makes its helpers
    // (e.g. sharedKmpTargets()) available to every module's build script.
    id("meals.kmp.domain") apply false
    id("meals.kmp.data") apply false
    id("meals.kmp.feature.ui") apply false
    id("meals.detekt") apply false
}

// The included build-logic's tests, under the name the modules' JVM tests have,
// so `./gradlew jvmTest` (and CI) runs them too.
tasks.register("jvmTest") {
    group = "verification"
    description = "Runs the tests of the build-logic included build."
    dependsOn(gradle.includedBuild("build-logic").task(":convention:test"))
}

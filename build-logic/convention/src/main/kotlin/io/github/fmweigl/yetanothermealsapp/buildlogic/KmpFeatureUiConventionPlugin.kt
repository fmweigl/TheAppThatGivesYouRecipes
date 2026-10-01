package io.github.fmweigl.yetanothermealsapp.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Compose Multiplatform feature UI modules: shared targets plus Android, Compose, lifecycle and Koin
 * ViewModel support. Each module sets its own `namespace` in `kotlin { android { } }`.
 */
class KmpFeatureUiConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.apply("meals.detekt")

        extensions.configure<KotlinMultiplatformExtension> {
            sharedKmpTargets()
            androidLibraryDefaults(project)

            sourceSets.commonMain.dependencies {
                implementation(libs.lib("compose-runtime"))
                implementation(libs.lib("compose-foundation"))
                implementation(libs.lib("compose-material3"))
                implementation(libs.lib("compose-ui"))
                implementation(libs.lib("androidx-lifecycle-viewmodelCompose"))
                implementation(libs.lib("androidx-lifecycle-runtimeCompose"))
                implementation(libs.lib("koin-composeViewmodel"))
            }
            sourceSets.commonTest.dependencies {
                implementation(libs.lib("kotlin-test"))
                implementation(libs.lib("kotlinx-coroutinesTest"))
            }
        }
    }
}

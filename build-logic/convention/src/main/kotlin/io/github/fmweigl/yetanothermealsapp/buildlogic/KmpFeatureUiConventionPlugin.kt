package io.github.fmweigl.yetanothermealsapp.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Compose Multiplatform feature UI modules: everything from `meals.kmp.compose`, plus lifecycle,
 * Koin ViewModel and Navigation 3 support (kotlinx.serialization for the `NavKey`s), Compose
 * resources for the module's strings, and the design system. Each module sets its own `namespace`
 * in `kotlin { android { } }` and its `compose.resources { packageOfResClass }` (the default
 * package comes from the project name, `ui`, which every feature shares).
 */
class KmpFeatureUiConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("meals.kmp.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            // Needed to package the module's Compose resources into the Android app.
            (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
                androidResources.enable = true
            }
            sourceSets.commonMain.dependencies {
                implementation(libs.lib("compose-components-resources"))
                implementation(project(":core:designsystem"))
                implementation(libs.lib("androidx-lifecycle-viewmodelCompose"))
                implementation(libs.lib("androidx-lifecycle-runtimeCompose"))
                implementation(libs.lib("koin-composeViewmodel"))
                implementation(libs.lib("androidx-navigation3-runtime"))
            }
            sourceSets.commonTest.dependencies {
                implementation(libs.lib("kotlinx-coroutinesTest"))
            }
        }
    }
}

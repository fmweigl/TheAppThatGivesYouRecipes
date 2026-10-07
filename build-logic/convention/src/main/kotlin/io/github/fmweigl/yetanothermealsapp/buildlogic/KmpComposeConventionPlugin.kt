package io.github.fmweigl.yetanothermealsapp.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Compose Multiplatform library modules: shared targets plus Android, with Compose runtime,
 * foundation, material3 and ui, and Compose resources for the module's strings. `@Preview`s live in
 * commonMain (ui-tooling-preview); Android Studio renders them through the `android` target, which
 * gets ui-tooling on its runtime classpath only (never packaged into the app). Each module sets
 * its own `namespace` in `kotlin { android { } }` and, if it has resources, its
 * `compose.resources { packageOfResClass }`. Feature `ui` modules get this through
 * `meals.kmp.feature.ui`.
 */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        pluginManager.apply("meals.detekt")

        extensions.configure<KotlinMultiplatformExtension> {
            sharedKmpTargets()
            androidLibraryDefaults(project)
            // Needed to package the module's Compose resources into the Android app.
            (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
                androidResources.enable = true
            }

            sourceSets.commonMain.dependencies {
                implementation(libs.lib("compose-components-resources"))
                implementation(libs.lib("compose-runtime"))
                implementation(libs.lib("compose-foundation"))
                implementation(libs.lib("compose-material3"))
                implementation(libs.lib("compose-ui"))
                implementation(libs.lib("compose-uiToolingPreview"))
            }
            sourceSets.commonTest.dependencies {
                implementation(libs.lib("kotlin-test"))
            }
        }
        dependencies.addProvider("androidRuntimeClasspath", libs.lib("compose-uiTooling"))
    }
}

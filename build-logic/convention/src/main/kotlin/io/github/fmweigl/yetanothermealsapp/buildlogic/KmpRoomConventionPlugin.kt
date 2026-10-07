package io.github.fmweigl.yetanothermealsapp.buildlogic

import androidx.room3.gradle.RoomExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

/**
 * Data modules with a Room database: everything from `meals.kmp.data`, plus an `android` target
 * (Room on Android needs a `Context` and Android-specific generated code, so the `jvm` variant
 * other data modules give Android won't do), Room with the bundled SQLite driver, and Room's
 * compiler on every target through KSP. Database schemas are exported to `schemas/` and committed.
 * Each module sets its own `namespace` in `kotlin { android { } }`.
 */
class KmpRoomConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("meals.kmp.data")
        pluginManager.apply("com.android.kotlin.multiplatform.library")
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("androidx.room3")

        extensions.configure<KotlinMultiplatformExtension> {
            androidLibraryDefaults(project)

            sourceSets.commonMain.dependencies {
                implementation(libs.lib("room3-runtime"))
                implementation(libs.lib("sqlite-bundled"))
            }

            // One KSP configuration per target (kspAndroid, kspJvm, kspIosArm64, ...).
            targets.matching { it.platformType != KotlinPlatformType.common }.configureEach {
                val kspConfiguration = "ksp" + name.replaceFirstChar { it.uppercase() }
                dependencies.add(kspConfiguration, libs.lib("room3-compiler"))
            }
        }

        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/schemas")
        }
    }
}

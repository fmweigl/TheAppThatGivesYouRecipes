package io.github.fmweigl.yetanothermealsapp.buildlogic

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * The targets every KMP module builds for. This is the only place the list is declared;
 * UI modules add `android` on top through [androidLibraryDefaults].
 */
fun KotlinMultiplatformExtension.sharedKmpTargets() {
    jvm {
        // Android uses the jvm variant of modules without an android target, and an android
        // compilation (JVM 11) can't inline functions compiled for a newer JVM.
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosArm64()
    iosSimulatorArm64()
}

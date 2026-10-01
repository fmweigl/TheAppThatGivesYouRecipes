package io.github.fmweigl.yetanothermealsapp.buildlogic

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * The targets every KMP module builds for. This is the only place the list is declared;
 * UI modules add `android` on top through [androidLibraryDefaults].
 */
fun KotlinMultiplatformExtension.sharedKmpTargets() {
    jvm()
    iosArm64()
    iosSimulatorArm64()
}

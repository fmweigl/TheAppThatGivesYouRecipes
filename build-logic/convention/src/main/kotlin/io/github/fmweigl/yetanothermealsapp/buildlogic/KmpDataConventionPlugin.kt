package io.github.fmweigl.yetanothermealsapp.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Data modules: shared targets, kotlinx.serialization, and the test libraries for `MockEngine` tests. */
class KmpDataConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        pluginManager.apply("meals.detekt")

        extensions.configure<KotlinMultiplatformExtension> {
            sharedKmpTargets()
            sourceSets.commonTest.dependencies {
                implementation(libs.lib("kotlin-test"))
                implementation(libs.lib("kotlinx-coroutinesTest"))
                implementation(libs.lib("ktor-clientMock"))
            }
        }
    }
}

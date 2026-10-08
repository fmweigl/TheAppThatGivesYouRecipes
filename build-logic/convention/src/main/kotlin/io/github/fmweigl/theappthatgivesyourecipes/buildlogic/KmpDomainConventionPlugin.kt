package io.github.fmweigl.theappthatgivesyourecipes.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Pure Kotlin domain modules: shared targets, kotlin-test, and [VerifyDomainDependencies] on `check`. */
class KmpDomainConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("meals.detekt")

        extensions.configure<KotlinMultiplatformExtension> {
            sharedKmpTargets()
            sourceSets.commonTest.dependencies {
                implementation(libs.lib("kotlin-test"))
            }
        }

        registerVerifyDomainDependencies()
    }
}

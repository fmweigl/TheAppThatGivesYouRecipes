package io.github.fmweigl.yetanothermealsapp.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Configures the `android` target of a module using `com.android.kotlin.multiplatform.library`:
 * SDK levels from the version catalog and JVM target 11. Each module still sets its own `namespace`.
 */
fun KotlinMultiplatformExtension.androidLibraryDefaults(project: Project) {
    val libs = project.libs
    (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        compileSdk = libs.version("android-compileSdk").toInt()
        minSdk = libs.version("android-minSdk").toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

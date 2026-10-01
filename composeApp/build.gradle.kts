import io.github.fmweigl.yetanothermealsapp.buildlogic.androidLibraryDefaults
import io.github.fmweigl.yetanothermealsapp.buildlogic.sharedKmpTargets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("meals.detekt")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sharedKmpTargets()
    androidLibraryDefaults(project)

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "io.github.fmweigl.yetanothermealsapp.composeapp"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.network)
            implementation(projects.feature.about.ui)
            implementation(projects.feature.randomrecipe.data)
            implementation(projects.feature.randomrecipe.ui)

            api(libs.koin.core)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsCore)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodelNavigation3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

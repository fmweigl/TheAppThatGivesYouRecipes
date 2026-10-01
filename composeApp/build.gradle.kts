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
    alias(libs.plugins.aboutLibraries)
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
        // Needed to package the Compose resources (aboutlibraries.json) into the Android app.
        androidResources.enable = true
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
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodelNavigation3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// The About tab lists every library the app ships with. The plugin collects them (with their
// licenses) from this module, which depends on all others, into build/generated/aboutLibrariesResources.
// That directory is commonMain's Compose resources directory, so the JSON is regenerated on every
// build and read at runtime as `Res.readBytes("files/aboutlibraries.json")`.
aboutLibraries {
    collect {
        // Only what ships: the Android and desktop classpaths and iOS's dependencies. Leaves out the
        // tooling configurations (hot reload's jvmDev, ...).
        filterVariants.addAll("android", "jvm", "metadataIosMain")
    }
    export {
        outputFile = layout.buildDirectory.file("generated/aboutLibrariesResources/files/aboutlibraries.json")
    }
}

compose.resources {
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = tasks.named("exportLibraryDefinitions").map {
            layout.buildDirectory.dir("generated/aboutLibrariesResources").get()
        },
    )
}

import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.androidLibraryDefaults
import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.appVersionSource
import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.requireTheMealDbProductionKey
import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.sharedKmpTargets
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
    appVersionSource(project, packageName = "io.github.fmweigl.theappthatgivesyourecipes")
    androidLibraryDefaults(project)

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "io.github.fmweigl.theappthatgivesyourecipes.composeapp"
        // Needed to package the Compose resources (aboutlibraries.json) into the Android app.
        androidResources.enable = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(projects.core.network)
            implementation(projects.feature.about.ui)
            implementation(projects.feature.favorites.ui)
            implementation(projects.feature.recipe.data)
            implementation(projects.feature.recipe.ui)
            implementation(projects.feature.search.ui)

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

// The About tab shows the app's license and every library it ships with. These files are
// commonMain's Compose resources, generated on every build into build/generated/appComposeResources
// (together with the strings from src/commonMain/composeResources) and read at runtime with
// `Res.readBytes("files/...")`:
// - files/aboutlibraries.json: the AboutLibraries plugin collects the libraries (with their
//   licenses) from this module, which depends on all others.
// - files/LICENSE, files/PRIVACY.md and files/ATTRIBUTIONS.md: copied from the repository root, so
//   the app shows the same license, privacy policy and attributions as the repository.
aboutLibraries {
    collect {
        // Only what ships: the Android and desktop classpaths and iOS's dependencies. Leaves out the
        // tooling configurations (hot reload's jvmDev, ...).
        filterVariants.addAll("android", "jvm", "metadataIosMain")
    }
    export {
        outputFile = layout.buildDirectory.file("generated/aboutLibrariesExport/aboutlibraries.json")
    }
}

val generateAppComposeResources by tasks.registering(Sync::class) {
    // The hand-written resources (strings), which the custom directory below would otherwise hide.
    from("src/commonMain/composeResources")
    into("files") {
        from(tasks.named("exportLibraryDefinitions"))
        from(rootProject.layout.projectDirectory.file("LICENSE"))
        from(rootProject.layout.projectDirectory.file("PRIVACY.md"))
        from(rootProject.layout.projectDirectory.file("ATTRIBUTIONS.md"))
    }
    into(layout.buildDirectory.dir("generated/appComposeResources"))
}

compose.resources {
    customDirectory(
        sourceSetName = "commonMain",
        directoryProvider = layout.dir(generateAppComposeResources.map { it.destinationDir }),
    )
}

// iOS release frameworks (Xcode's Release configuration) must not ship TheMealDB's test key.
requireTheMealDbProductionKey { it.startsWith("linkRelease") }

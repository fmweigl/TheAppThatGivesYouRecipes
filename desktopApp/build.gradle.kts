import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.appVersion
import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.requireDesktopPackagableVersion
import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.requireTheMealDbProductionKey
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("meals.detekt")
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.composeApp)

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.components.resources)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

val appVersion = appVersion().get()

compose.desktop {
    application {
        mainClass = "io.github.fmweigl.theappthatgivesyourecipes.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.fmweigl.theappthatgivesyourecipes"
            packageVersion = appVersion.name
            // Rendered by art/app-icon/render_icons.py.
            linux { iconFile.set(project.file("src/main/resources/icon.png")) }
            macOS {
                iconFile.set(project.file("icons/icon.icns"))
                packageBuildVersion = appVersion.code.toString()
            }
            windows {
                iconFile.set(project.file("icons/icon.ico"))
                msiPackageVersion = appVersion.msiPackageVersion
            }
        }
    }
}

// MSI/EXE installers allow at most 65535 in the version's third number.
requireDesktopPackagableVersion()

// Release builds (runRelease, packageRelease*, ...) must not ship TheMealDB's test key.
requireTheMealDbProductionKey { "Release" in it }

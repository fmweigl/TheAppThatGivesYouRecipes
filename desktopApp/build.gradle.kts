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

compose.desktop {
    application {
        mainClass = "io.github.fmweigl.yetanothermealsapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.fmweigl.yetanothermealsapp"
            packageVersion = "1.0.0"
            // Rendered by art/app-icon/render_icons.py.
            linux { iconFile.set(project.file("src/main/resources/icon.png")) }
            macOS { iconFile.set(project.file("icons/icon.icns")) }
            windows { iconFile.set(project.file("icons/icon.ico")) }
        }
    }
}
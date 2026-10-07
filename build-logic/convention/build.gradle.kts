plugins {
    `kotlin-dsl`
}

/** The plugin's marker artifact, so the plugins come from the existing `[plugins]` catalog entries. */
fun Provider<PluginDependency>.asDependency(): Provider<String> =
    map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}" }

dependencies {
    // compileOnly: at runtime the plugins come from the root build's classloader (see its `apply false` list).
    compileOnly(libs.plugins.kotlinMultiplatform.asDependency())
    compileOnly(libs.plugins.androidMultiplatformLibrary.asDependency())
    compileOnly(libs.plugins.composeMultiplatform.asDependency())
    compileOnly(libs.plugins.composeCompiler.asDependency())
    compileOnly(libs.plugins.kotlinSerialization.asDependency())
    compileOnly(libs.plugins.detekt.asDependency())
    compileOnly(libs.plugins.ksp.asDependency())
    compileOnly(libs.plugins.room3.asDependency())
}

gradlePlugin {
    plugins {
        register("kmpDomain") {
            id = "meals.kmp.domain"
            implementationClass = "io.github.fmweigl.yetanothermealsapp.buildlogic.KmpDomainConventionPlugin"
        }
        register("kmpData") {
            id = "meals.kmp.data"
            implementationClass = "io.github.fmweigl.yetanothermealsapp.buildlogic.KmpDataConventionPlugin"
        }
        register("kmpRoom") {
            id = "meals.kmp.room"
            implementationClass = "io.github.fmweigl.yetanothermealsapp.buildlogic.KmpRoomConventionPlugin"
        }
        register("detekt") {
            id = "meals.detekt"
            implementationClass = "io.github.fmweigl.yetanothermealsapp.buildlogic.DetektConventionPlugin"
        }
        register("kmpCompose") {
            id = "meals.kmp.compose"
            implementationClass = "io.github.fmweigl.yetanothermealsapp.buildlogic.KmpComposeConventionPlugin"
        }
        register("kmpFeatureUi") {
            id = "meals.kmp.feature.ui"
            implementationClass = "io.github.fmweigl.yetanothermealsapp.buildlogic.KmpFeatureUiConventionPlugin"
        }
    }
}

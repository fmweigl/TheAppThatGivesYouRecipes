plugins {
    id("meals.kmp.feature.ui")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.yetanothermealsapp.recipe.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.recipe.domain)

            implementation(libs.compose.materialIconsCore)
            implementation(libs.compose.material3Adaptive)
            api(libs.koin.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.networkKtor)
        }
        // Semantics tests (screen reader output) run on the JVM, with Skia from the desktop runtime.
        jvmTest.dependencies {
            implementation(libs.compose.uiTest)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.yetanothermealsapp.recipe.ui.resources"
}

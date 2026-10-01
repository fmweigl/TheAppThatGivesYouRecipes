plugins {
    id("meals.kmp.feature.ui")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.yetanothermealsapp.randomrecipe.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.randomrecipe.domain)

            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.materialIconsCore)
            api(libs.koin.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.networkKtor)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources"
}

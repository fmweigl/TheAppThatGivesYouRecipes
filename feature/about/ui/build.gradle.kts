plugins {
    id("meals.kmp.feature.ui")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.yetanothermealsapp.about.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.aboutlibraries.composeM3)
            implementation(libs.compose.materialIconsCore)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.yetanothermealsapp.about.ui.resources"
}

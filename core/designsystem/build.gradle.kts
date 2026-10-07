plugins {
    id("meals.kmp.compose")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.yetanothermealsapp.core.designsystem"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.materialIconsCore)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.yetanothermealsapp.core.designsystem.resources"
}

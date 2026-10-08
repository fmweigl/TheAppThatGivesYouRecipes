plugins {
    id("meals.kmp.compose")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.theappthatgivesyourecipes.core.designsystem"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.materialIconsCore)
        }
        // Component tests run on the JVM, with Skia from the desktop runtime.
        jvmTest.dependencies {
            implementation(libs.compose.uiTest)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.resources"
}

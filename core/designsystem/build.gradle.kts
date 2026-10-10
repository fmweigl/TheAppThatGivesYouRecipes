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
        // WindowSizes.kt: test helpers for the feature modules' jvmTest. compileOnly, so the
        // desktop app doesn't ship ui-test; the modules' tests add it themselves.
        jvmMain.dependencies {
            compileOnly(libs.compose.uiTest)
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

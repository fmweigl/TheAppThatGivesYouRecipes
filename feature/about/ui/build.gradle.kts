plugins {
    id("meals.kmp.feature.ui")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.theappthatgivesyourecipes.about.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.aboutlibraries.composeM3)
        }
        // Semantics tests (screen reader output) run on the JVM, with Skia from the desktop runtime.
        jvmTest.dependencies {
            implementation(libs.compose.uiTest)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources"
}

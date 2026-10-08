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
    }
}

compose.resources {
    packageOfResClass = "io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources"
}

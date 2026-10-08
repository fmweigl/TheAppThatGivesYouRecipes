import io.github.fmweigl.theappthatgivesyourecipes.buildlogic.theMealDbApiKeySource

plugins {
    id("meals.kmp.data")
}

kotlin {
    theMealDbApiKeySource(project, packageName = "io.github.fmweigl.theappthatgivesyourecipes.core.network")

    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.clientCore)
            api(libs.koin.core)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.serializationKotlinxJson)
            implementation(libs.kotlinx.serializationJson)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.clientOkhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.clientDarwin)
        }
    }
}

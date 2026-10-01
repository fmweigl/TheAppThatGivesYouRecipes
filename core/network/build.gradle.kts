plugins {
    id("meals.kmp.data")
}

kotlin {
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

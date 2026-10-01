plugins {
    id("meals.kmp.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)

            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.kotlinx.serializationJson)
        }
        commonTest.dependencies {
            implementation(libs.ktor.serializationKotlinxJson)
        }
    }
}

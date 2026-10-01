plugins {
    id("meals.kmp.data")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.network)
            implementation(projects.feature.randomrecipe.domain)

            implementation(libs.ktor.clientCore)
            api(libs.koin.core)
            implementation(libs.kotlinx.serializationJson)
        }
    }
}

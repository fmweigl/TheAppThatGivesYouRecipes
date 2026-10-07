plugins {
    id("meals.kmp.room")
}

kotlin {
    android {
        namespace = "io.github.fmweigl.yetanothermealsapp.recipe.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.network)
            implementation(projects.feature.recipe.domain)

            implementation(libs.ktor.clientCore)
            api(libs.koin.core)
            implementation(libs.kotlinx.serializationJson)
        }
    }
}

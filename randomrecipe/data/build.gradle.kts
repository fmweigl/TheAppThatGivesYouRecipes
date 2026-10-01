plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.randomrecipe.domain)

            implementation(libs.ktor.clientCore)
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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.clientMock)
            implementation(libs.kotlinx.coroutinesTest)
        }
    }
}

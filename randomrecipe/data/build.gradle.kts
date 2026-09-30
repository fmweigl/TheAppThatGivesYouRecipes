plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.randomrecipe.domain)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

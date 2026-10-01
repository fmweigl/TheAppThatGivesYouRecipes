import io.github.fmweigl.yetanothermealsapp.buildlogic.releaseSigning
import io.github.fmweigl.yetanothermealsapp.buildlogic.requireTheMealDbProductionKey
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("meals.detekt")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.composeApp)

    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "io.github.fmweigl.yetanothermealsapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.fmweigl.yetanothermealsapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    // The upload key from local.properties (or Gradle properties / environment variables).
    releaseSigning(project)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Release builds must not ship TheMealDB's test key. Every release task depends on preReleaseBuild.
requireTheMealDbProductionKey { it == "preReleaseBuild" }

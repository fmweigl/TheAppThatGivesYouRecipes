package com.example.yetanothermealsapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.yetanothermealsapp.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "YetAnotherMealsApp",
        ) {
            App()
        }
    }
}

package io.github.fmweigl.yetanothermealsapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fmweigl.yetanothermealsapp.di.initKoin

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

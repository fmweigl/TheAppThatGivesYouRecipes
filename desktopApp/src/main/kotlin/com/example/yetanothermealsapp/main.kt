package com.example.yetanothermealsapp

import androidx.compose.ui.window.Window
import com.example.yetanothermealsapp.randomrecipe.ui.App
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "YetAnotherMealsApp",
    ) {
        App()
    }
}
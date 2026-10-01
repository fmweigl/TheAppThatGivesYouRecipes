package io.github.fmweigl.yetanothermealsapp

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fmweigl.yetanothermealsapp.di.initKoin
import org.jetbrains.skia.Image

fun main() {
    initKoin()
    val icon = BitmapPainter(Image.makeFromEncoded(readResource("icon.png")).toComposeImageBitmap())
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "YetAnotherMealsApp",
            icon = icon,
        ) {
            App()
        }
    }
}

private fun readResource(name: String): ByteArray =
    checkNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream(name)) { "Missing resource $name" }
        .use { it.readBytes() }

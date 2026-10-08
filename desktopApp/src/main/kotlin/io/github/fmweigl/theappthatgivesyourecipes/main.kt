package io.github.fmweigl.theappthatgivesyourecipes

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fmweigl.theappthatgivesyourecipes.di.initKoin
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skia.Image
import theappthatgivesyourecipes.desktopapp.generated.resources.Res
import theappthatgivesyourecipes.desktopapp.generated.resources.app_name

fun main() {
    initKoin()
    val icon = BitmapPainter(Image.makeFromEncoded(readResource("icon.png")).toComposeImageBitmap())
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            icon = icon,
        ) {
            App()
        }
    }
}

private fun readResource(name: String): ByteArray =
    checkNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream(name)) { "Missing resource $name" }
        .use { it.readBytes() }

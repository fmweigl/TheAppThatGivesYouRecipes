package io.github.fmweigl.yetanothermealsapp.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.test.Test
import kotlin.test.assertTrue

class ColorContrastTest {

    @Test
    fun lightSchemeMeetsTextContrast() = assertTextContrast(LightColorScheme)

    @Test
    fun darkSchemeMeetsTextContrast() = assertTextContrast(DarkColorScheme)

    private fun assertTextContrast(scheme: ColorScheme) = with(scheme) {
        val pairs = mapOf(
            "primary" to (onPrimary to primary),
            "primaryContainer" to (onPrimaryContainer to primaryContainer),
            "secondary" to (onSecondary to secondary),
            "secondaryContainer" to (onSecondaryContainer to secondaryContainer),
            "tertiary" to (onTertiary to tertiary),
            "tertiaryContainer" to (onTertiaryContainer to tertiaryContainer),
            "error" to (onError to error),
            "errorContainer" to (onErrorContainer to errorContainer),
            "background" to (onBackground to background),
            "surface" to (onSurface to surface),
            "surfaceVariant" to (onSurfaceVariant to surfaceVariant),
            "surfaceContainerHighest" to (onSurfaceVariant to surfaceContainerHighest),
            "inverseSurface" to (inverseOnSurface to inverseSurface),
            "error on surface" to (error to surface),
        )
        pairs.forEach { (name, colors) ->
            val ratio = contrast(colors.first, colors.second)
            assertTrue(ratio >= MIN_TEXT_CONTRAST, "$name: contrast $ratio is below $MIN_TEXT_CONTRAST")
        }
    }

    private fun contrast(a: Color, b: Color): Float {
        val (light, dark) = listOf(a.luminance(), b.luminance()).sortedDescending()
        return (light + 0.05f) / (dark + 0.05f)
    }

    private companion object {
        /** WCAG AA for normal text. */
        const val MIN_TEXT_CONTRAST = 4.5f
    }
}

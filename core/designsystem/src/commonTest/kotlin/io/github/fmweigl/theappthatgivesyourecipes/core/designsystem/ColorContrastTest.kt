package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.SKELETON_MIN_ALPHA
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
            // Secondary text directly on a screen, such as the About screen's version line.
            "onSurfaceVariant on background" to (onSurfaceVariant to background),
            "onSurfaceVariant on surface" to (onSurfaceVariant to surface),
            "inverseSurface" to (inverseOnSurface to inverseSurface),
            "error on surface" to (error to surface),
        )
        pairs.forEach { (name, colors) ->
            val ratio = contrast(colors.first, colors.second)
            assertTrue(ratio >= MIN_TEXT_CONTRAST, "$name: contrast $ratio is below $MIN_TEXT_CONTRAST")
        }
    }

    @Test
    fun lightSchemeMeetsIconContrast() = assertIconContrast(LightColorScheme)

    @Test
    fun darkSchemeMeetsIconContrast() = assertIconContrast(DarkColorScheme)

    /** Icons drawn in a color other than the on-color: the filled heart on screens and cards. */
    private fun assertIconContrast(scheme: ColorScheme) = with(scheme) {
        val pairs = mapOf(
            "primary on background" to (primary to background),
            "primary on surface" to (primary to surface),
            "primary on surfaceContainerHighest" to (primary to surfaceContainerHighest),
        )
        pairs.forEach { (name, colors) ->
            val ratio = contrast(colors.first, colors.second)
            assertTrue(ratio >= MIN_ICON_CONTRAST, "$name: contrast $ratio is below $MIN_ICON_CONTRAST")
        }
    }

    @Test
    fun lightSkeletonIsVisible() = assertSkeletonContrast(LightColorScheme)

    @Test
    fun darkSkeletonIsVisible() = assertSkeletonContrast(DarkColorScheme)

    /**
     * The skeleton's blocks are the only sign that a screen is loading, so they have to stand out
     * from the background, also at the low point of their pulse. Decorative shapes have no WCAG
     * minimum; this keeps them from fading away by accident.
     */
    private fun assertSkeletonContrast(scheme: ColorScheme) = with(scheme) {
        val full = contrast(outlineVariant, background)
        val faded = contrast(outlineVariant.copy(alpha = SKELETON_MIN_ALPHA).compositeOver(background), background)
        assertTrue(full >= MIN_SKELETON_CONTRAST, "skeleton: contrast $full is below $MIN_SKELETON_CONTRAST")
        assertTrue(
            faded >= MIN_FADED_SKELETON_CONTRAST,
            "faded skeleton: contrast $faded is below $MIN_FADED_SKELETON_CONTRAST",
        )
    }

    private fun contrast(a: Color, b: Color): Float {
        val (light, dark) = listOf(a.luminance(), b.luminance()).sortedDescending()
        return (light + 0.05f) / (dark + 0.05f)
    }

    private companion object {
        /** WCAG AA for normal text. */
        const val MIN_TEXT_CONTRAST = 4.5f

        /** WCAG AA for graphical objects such as icons (1.4.11). */
        const val MIN_ICON_CONTRAST = 3f

        /** The skeleton's blocks at full opacity and at the low point of their pulse. */
        const val MIN_SKELETON_CONTRAST = 1.5f
        const val MIN_FADED_SKELETON_CONTRAST = 1.4f
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The app's Material 3 theme: the food palette from `Color.kt`, serif headlines and rounded
 * shapes. Follows the system's dark mode. Screens read it through `MaterialTheme`, never through
 * the color values directly.
 */
@Composable
fun MealsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = MealsTypography,
        shapes = MealsShapes,
        content = content,
    )
}

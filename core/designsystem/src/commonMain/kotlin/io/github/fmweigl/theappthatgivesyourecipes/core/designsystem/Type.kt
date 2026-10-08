package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

private val Default = Typography()

/** A serif face for display, headline and large titles (cookbook style); body and labels stay sans-serif. */
private fun TextStyle.serif(weight: FontWeight) = copy(fontFamily = FontFamily.Serif, fontWeight = weight)

internal val MealsTypography = Typography(
    displayLarge = Default.displayLarge.serif(FontWeight.SemiBold),
    displayMedium = Default.displayMedium.serif(FontWeight.SemiBold),
    displaySmall = Default.displaySmall.serif(FontWeight.SemiBold),
    headlineLarge = Default.headlineLarge.serif(FontWeight.SemiBold),
    headlineMedium = Default.headlineMedium.serif(FontWeight.SemiBold),
    headlineSmall = Default.headlineSmall.serif(FontWeight.SemiBold),
    titleLarge = Default.titleLarge.serif(FontWeight.SemiBold),
    titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Default.titleSmall.copy(fontWeight = FontWeight.SemiBold),
)

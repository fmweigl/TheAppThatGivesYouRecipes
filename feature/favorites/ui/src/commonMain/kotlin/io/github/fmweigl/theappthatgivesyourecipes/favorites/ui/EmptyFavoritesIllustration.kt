package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val CardWidth = 132.dp
private val CardImageHeight = 72.dp
private val CardLineHeight = 10.dp
private val HeartSize = 28.dp
private val BurstRadius = 22.dp
private val BurstDotRadius = 2.5.dp

private const val BURST_DOTS = 8
private const val BURST_ALPHA = 0.6f
private const val CARD_LINE_ALPHA = 0.3f
private const val SHORT_LINE_FRACTION = 0.6f

/**
 * A saved recipe in miniature: a card with a picture, two lines of text and a filled heart, with
 * the dots of the heart's save animation around it. Decorative: the text below says how to save a
 * recipe, so screen readers skip it. Drawn from the theme's colors and icons, so it follows dark mode.
 */
@Composable
internal fun EmptyFavoritesIllustration(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clearAndSetSemantics {}
            .width(CardWidth)
            .background(colors.surfaceContainerHighest, MaterialTheme.shapes.large)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(CardImageHeight)
                .background(colors.secondaryContainer, MaterialTheme.shapes.medium),
        )
        CardLine(Modifier.fillMaxWidth())
        CardLine(Modifier.fillMaxWidth(SHORT_LINE_FRACTION))
        val heartColor = colors.primary
        Icon(
            Icons.Filled.Favorite,
            contentDescription = null,
            tint = heartColor,
            modifier = Modifier
                .align(Alignment.End)
                .size(HeartSize)
                .drawBehind {
                    repeat(BURST_DOTS) { index ->
                        val angle = 2 * PI * index / BURST_DOTS
                        val radius = BurstRadius.toPx()
                        drawCircle(
                            heartColor,
                            radius = BurstDotRadius.toPx(),
                            center = center + Offset((cos(angle) * radius).toFloat(), (sin(angle) * radius).toFloat()),
                            alpha = BURST_ALPHA,
                        )
                    }
                },
        )
    }
}

@Composable
private fun CardLine(modifier: Modifier) {
    Box(
        modifier
            .height(CardLineHeight)
            .background(
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = CARD_LINE_ALPHA),
                MaterialTheme.shapes.extraSmall,
            ),
    )
}

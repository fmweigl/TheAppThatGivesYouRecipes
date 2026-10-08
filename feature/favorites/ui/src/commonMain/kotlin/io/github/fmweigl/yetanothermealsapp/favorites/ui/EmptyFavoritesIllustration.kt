package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.favorites
import org.jetbrains.compose.resources.stringResource

private val CardWidth = 72.dp
private val CardImageHeight = 40.dp
private val CardLineHeight = 6.dp
private val CardHeartSize = 18.dp
private val TapRingRadius = 14.dp
private val TapDotRadius = 8.dp
private val TabPillWidth = 56.dp
private val TabPillHeight = 32.dp

private const val TAP_RING_ALPHA = 0.5f
private const val TAP_DOT_ALPHA = 0.25f
private const val CARD_LINE_ALPHA = 0.3f
private const val SHORT_LINE_FRACTION = 0.6f

/**
 * How a recipe becomes a favorite, in three steps: a recipe with its heart being tapped, the same
 * recipe with the heart filled, and the Favorites tab. Decorative: the text below says the same,
 * so screen readers skip it. Drawn from the theme's colors and icons, so it follows dark mode.
 */
@Composable
internal fun EmptyFavoritesIllustration(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiniRecipeCard(isFavorite = false)
        StepArrow()
        MiniRecipeCard(isFavorite = true)
        StepArrow()
        FavoritesTab()
    }
}

/** A recipe card in miniature: image, two lines of text and the heart, tapped while it's empty. */
@Composable
private fun MiniRecipeCard(isFavorite: Boolean) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .width(CardWidth)
            .background(colors.surfaceContainerHighest, MaterialTheme.shapes.medium)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(CardImageHeight)
                .background(colors.secondaryContainer, MaterialTheme.shapes.small),
        )
        CardLine(Modifier.fillMaxWidth())
        CardLine(Modifier.fillMaxWidth(SHORT_LINE_FRACTION))
        val tapColor = colors.primary
        Icon(
            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) colors.primary else colors.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .size(CardHeartSize)
                .drawBehind {
                    if (isFavorite) return@drawBehind
                    // A fingertip's tap: a soft dot and a ring around the heart.
                    drawCircle(tapColor, radius = TapDotRadius.toPx(), alpha = TAP_DOT_ALPHA)
                    drawCircle(
                        tapColor,
                        radius = TapRingRadius.toPx(),
                        alpha = TAP_RING_ALPHA,
                        style = Stroke(width = 1.5.dp.toPx()),
                    )
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

@Composable
private fun StepArrow() {
    Icon(
        Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** The Favorites tab as the navigation bar shows it when selected: a pill with a heart, and its label. */
@Composable
private fun FavoritesTab() {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(TabPillWidth, TabPillHeight)
                .background(colors.secondaryContainer, MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = null, tint = colors.onSecondaryContainer)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(Res.string.favorites),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurface,
        )
    }
}

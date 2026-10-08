package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.favorite
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ButtonSize = 56.dp
private val HeartSize = 32.dp
private val BurstDotRadius = 3.dp

private const val BURST_DOTS = 8
private const val BURST_MILLIS = 450

/** The heart starts this small when it fills, then springs past its size and settles. */
private const val POP_START_SCALE = 0.6f

/** The heart starts this small when it empties, then grows back without bouncing. */
private const val SHRINK_START_SCALE = 0.8f
private const val POP_DAMPING = 0.35f

/** Where the burst's dots start and end, as fractions of the button's radius. */
private const val BURST_START = 0.5f
private const val BURST_END = 1.1f

/**
 * A heart that saves or removes a favorite; screen readers get "Favorite" with its on/off state.
 * When it fills, it pops and a ring of dots bursts out of it; when it empties, it shrinks briefly.
 * Nothing animates when the screen first shows it. On Android the animations follow the system's
 * animation scale, so "Remove animations" turns them off.
 */
@Composable
internal fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    // 0 when the burst starts, 1 when it's gone (also when there's none).
    val burst = remember { Animatable(1f) }
    var shownState by remember { mutableStateOf(isFavorite) }
    LaunchedEffect(isFavorite) {
        if (isFavorite == shownState) return@LaunchedEffect
        shownState = isFavorite
        if (isFavorite) {
            launch {
                burst.snapTo(0f)
                burst.animateTo(1f, tween(BURST_MILLIS, easing = FastOutSlowInEasing))
            }
            scale.snapTo(POP_START_SCALE)
            scale.animateTo(1f, spring(dampingRatio = POP_DAMPING, stiffness = Spring.StiffnessMedium))
        } else {
            scale.snapTo(SHRINK_START_SCALE)
            scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
        }
    }
    val burstColor = MaterialTheme.colorScheme.primary
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onToggle() },
        modifier = modifier
            .size(ButtonSize)
            .drawBehind { drawBurst(burst.value, burstColor) },
    ) {
        Icon(
            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(Res.string.favorite),
            modifier = Modifier
                .size(HeartSize)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
        )
    }
}

/** Dots that move out from the heart, shrinking and fading, while [progress] goes from 0 to 1. */
private fun DrawScope.drawBurst(progress: Float, color: Color) {
    if (progress >= 1f) return
    val radius = size.minDimension / 2 * (BURST_START + (BURST_END - BURST_START) * progress)
    val remaining = 1f - progress
    repeat(BURST_DOTS) { index ->
        val angle = 2 * PI * index / BURST_DOTS
        drawCircle(
            color = color,
            radius = BurstDotRadius.toPx() * remaining,
            center = center + Offset((cos(angle) * radius).toFloat(), (sin(angle) * radius).toFloat()),
            alpha = remaining,
        )
    }
}

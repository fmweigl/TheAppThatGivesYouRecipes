package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.resources.favorite
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ButtonSize = 56.dp
private val HeartSize = 32.dp
private val BurstDotRadius = 3.dp
private val FocusRingWidth = 2.dp

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
 * When a tap fills it, it pops and a ring of dots bursts out of it; when a tap empties it, it
 * shrinks briefly. Only a tap animates: a change from elsewhere, such as the database answering
 * that a recipe shown after "Next" is saved, just changes the icon. On Android the animations
 * follow the system's animation scale ("Remove animations" turns them off); on iOS
 * [prefersReducedMotion] (Reduce Motion) does. With keyboard focus it gets a ring in `onSurface`,
 * since the button's own state layer (about 10 % of the content color) is too faint to see.
 */
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    // 0 when the burst starts, 1 when it's gone (also when there's none).
    val burst = remember { Animatable(1f) }
    var shownState by remember { mutableStateOf(isFavorite) }
    // Set by a tap, cleared by the change it causes, which is then animated.
    var tapped by remember { mutableStateOf(false) }
    val reduceMotion = prefersReducedMotion()
    LaunchedEffect(isFavorite) {
        if (isFavorite == shownState) return@LaunchedEffect
        shownState = isFavorite
        val animate = tapped && !reduceMotion
        tapped = false
        if (!animate) return@LaunchedEffect
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
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val ringColor = MaterialTheme.colorScheme.onSurface
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = {
            tapped = true
            onToggle()
        },
        modifier = modifier
            .size(ButtonSize)
            .drawBehind { drawBurst(burst.value, burstColor) }
            .then(if (focused) Modifier.border(FocusRingWidth, ringColor, CircleShape) else Modifier),
        interactionSource = interactionSource,
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

/** Stands in for a [FavoriteButton] in a [Skeleton]: a circle the heart's size, where the heart will be. */
@Composable
fun FavoriteButtonPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier.size(ButtonSize), contentAlignment = Alignment.Center) {
        SkeletonBlock(Modifier.size(HeartSize), CircleShape)
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

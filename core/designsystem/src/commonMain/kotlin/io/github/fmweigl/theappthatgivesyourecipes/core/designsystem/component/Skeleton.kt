package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Lowest opacity of the skeleton's pulse. Shallow, so the blocks stay visible against the
 * background throughout (`ColorContrastTest` checks it).
 */
internal const val SKELETON_MIN_ALPHA = 0.8f

/** One full pulse: from full opacity down to [SKELETON_MIN_ALPHA] and back. */
private const val PULSE_MILLIS = 1800

/**
 * A loading skeleton: [content] laid out from [SkeletonBlock]s where the real content will be. The
 * blocks pulse together, dipping from full opacity and back, so the pulse ends at full opacity:
 * where animations are turned off (Android's "Remove animations", iOS's Reduce Motion through
 * [prefersReducedMotion]) the skeleton stands still and fully visible. [content] gets the
 * skeleton's minimum size, so a content that fills its space fills the skeleton's.
 */
@Composable
fun Skeleton(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val alpha = if (prefersReducedMotion()) remember { mutableFloatStateOf(1f) } else rememberPulse()
    Box(modifier.graphicsLayer { this.alpha = alpha.value }, propagateMinConstraints = true) {
        content()
    }
}

@Composable
private fun rememberPulse(): State<Float> =
    rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = PULSE_MILLIS
                SKELETON_MIN_ALPHA at PULSE_MILLIS / 2
            },
        ),
    )

/** One placeholder shape of a [Skeleton], in [shape]: a line of text, an image, an icon. */
@Composable
fun SkeletonBlock(modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.small) {
    Box(modifier.background(MaterialTheme.colorScheme.outlineVariant, shape))
}

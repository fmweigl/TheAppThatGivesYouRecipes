package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Shared by the round icon buttons next to a recipe's name (FavoriteButton, ShareButton), so they
// line up and look alike.

/** The buttons' size: larger than Material's 48dp minimum, as they're the recipe's main actions. */
internal val RoundButtonSize = 56.dp

/** The icon inside a round button. */
internal val RoundButtonIconSize = 32.dp

private val FocusRingWidth = 2.dp

/**
 * A ring in `onSurface` while the button has keyboard focus, since the button's own state layer
 * (about 10 % of the content color) is too faint to see.
 */
@Composable
internal fun Modifier.focusRing(interactionSource: InteractionSource): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    return if (focused) border(FocusRingWidth, MaterialTheme.colorScheme.onSurface, CircleShape) else this
}

/** A circle the icon's size, where a round button's icon will be. */
@Composable
internal fun RoundButtonPlaceholder(modifier: Modifier) {
    Box(modifier.size(RoundButtonSize), contentAlignment = Alignment.Center) {
        SkeletonBlock(Modifier.size(RoundButtonIconSize), CircleShape)
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * A share icon, the same size as [FavoriteButton] so the two sit side by side, with the same focus
 * ring. [contentDescription] is what screen readers announce (the action, such as "Share recipe").
 * While [confirmed] is true it shows a check mark instead, e.g. right after something was copied.
 */
@Composable
fun ShareButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    confirmed: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    IconButton(
        onClick = onClick,
        modifier = modifier.size(RoundButtonSize).focusRing(interactionSource),
        interactionSource = interactionSource,
    ) {
        Icon(
            if (confirmed) Icons.Filled.Check else Icons.Filled.Share,
            contentDescription = contentDescription,
            modifier = Modifier.size(RoundButtonIconSize),
        )
    }
}

/** Stands in for a [ShareButton] in a [Skeleton]. */
@Composable
fun ShareButtonPlaceholder(modifier: Modifier = Modifier) {
    RoundButtonPlaceholder(modifier)
}

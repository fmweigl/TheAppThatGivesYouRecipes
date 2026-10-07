package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.favorite
import org.jetbrains.compose.resources.stringResource

/** A heart that saves or removes a favorite; screen readers get "Favorite" with its on/off state. */
@Composable
internal fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(checked = isFavorite, onCheckedChange = { onToggle() }, modifier = modifier) {
        Icon(
            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(Res.string.favorite),
        )
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Connects [FavoritesViewModel] to [FavoritesScreen]. */
@Composable
internal fun FavoritesRoute(
    onOpenRecipe: (recipeId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FavoritesScreen(
        uiState = uiState,
        onOpenRecipe = onOpenRecipe,
        onRemove = viewModel::remove,
        onRemovalMessageClosed = viewModel::removalMessageClosed,
        onRestoreFailureMessageClosed = viewModel::restoreFailureMessageClosed,
        onRemoveFailureMessageClosed = viewModel::removeFailureMessageClosed,
        modifier = modifier,
    )
}

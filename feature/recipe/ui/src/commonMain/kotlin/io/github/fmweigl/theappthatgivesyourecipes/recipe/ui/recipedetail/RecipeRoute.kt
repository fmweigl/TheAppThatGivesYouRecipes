package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Connects [RecipeViewModel] for [recipeId] to [RecipeScreen]. */
@Composable
internal fun RecipeRoute(
    recipeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeViewModel = koinViewModel { parametersOf(recipeId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RecipeScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onToggleFavorite = viewModel::toggleFavorite,
        modifier = modifier,
    )
}

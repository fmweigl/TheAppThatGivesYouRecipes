package io.github.fmweigl.yetanothermealsapp.recipe.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Connects [RandomRecipeViewModel] to [RandomRecipeScreen]. */
@Composable
internal fun RandomRecipeRoute(
    modifier: Modifier = Modifier,
    viewModel: RandomRecipeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RandomRecipeScreen(
        uiState = uiState,
        onShowNext = viewModel::showNext,
        onShowPrevious = viewModel::showPrevious,
        modifier = modifier,
    )
}

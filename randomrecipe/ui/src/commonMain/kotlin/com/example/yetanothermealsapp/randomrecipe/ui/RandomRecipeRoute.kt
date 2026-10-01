package com.example.yetanothermealsapp.randomrecipe.ui

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
        onLoadAnother = viewModel::loadRandomRecipe,
        modifier = modifier,
    )
}

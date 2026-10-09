package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe

import androidx.compose.runtime.Composable
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.preview.DevicePreviews
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.RecipePreview
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.previewRecipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe.RandomRecipeUiState.Content

@DevicePreviews
@Composable
private fun RandomRecipeScreenPreview() {
    RecipePreview {
        RandomRecipeScreen(
            uiState = RandomRecipeUiState(Content.Success(previewRecipe), canShowPrevious = true),
            onShowNext = {},
            onShowPrevious = {},
            onToggleFavorite = {},
        )
    }
}

@DevicePreviews
@Composable
private fun RandomRecipeScreenLoadingPreview() {
    RecipePreview {
        RandomRecipeScreen(
            uiState = RandomRecipeUiState(Content.Loading),
            onShowNext = {},
            onShowPrevious = {},
            onToggleFavorite = {},
        )
    }
}

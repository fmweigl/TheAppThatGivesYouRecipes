package io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe

import androidx.compose.runtime.Composable
import io.github.fmweigl.yetanothermealsapp.core.designsystem.preview.DevicePreviews
import io.github.fmweigl.yetanothermealsapp.recipe.ui.component.RecipePreview
import io.github.fmweigl.yetanothermealsapp.recipe.ui.component.previewRecipe
import io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe.RandomRecipeUiState.Content

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

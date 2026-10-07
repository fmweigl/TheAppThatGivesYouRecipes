package io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail

import androidx.compose.runtime.Composable
import io.github.fmweigl.yetanothermealsapp.core.designsystem.preview.DevicePreviews
import io.github.fmweigl.yetanothermealsapp.recipe.ui.component.RecipePreview
import io.github.fmweigl.yetanothermealsapp.recipe.ui.component.previewRecipe
import io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail.RecipeUiState.Content

@DevicePreviews
@Composable
private fun RecipeScreenPreview() {
    RecipePreview {
        RecipeScreen(
            uiState = RecipeUiState(Content.Success(previewRecipe), isFavorite = true),
            onBack = {},
            onRetry = {},
            onToggleFavorite = {},
        )
    }
}

package io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe

internal data class RecipeUiState(
    val content: Content = Content.Loading,
    val isFavorite: Boolean = false,
) {
    sealed interface Content {
        data object Loading : Content
        data class Success(val recipe: Recipe) : Content
        data class Error(val error: DataError) : Content
    }
}

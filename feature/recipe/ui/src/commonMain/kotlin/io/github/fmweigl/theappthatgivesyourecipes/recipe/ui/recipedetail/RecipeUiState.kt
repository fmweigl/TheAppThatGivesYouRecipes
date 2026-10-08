package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe

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

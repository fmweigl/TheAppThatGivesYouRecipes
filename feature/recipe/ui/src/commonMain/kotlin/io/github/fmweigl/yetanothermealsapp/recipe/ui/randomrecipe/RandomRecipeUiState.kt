package io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe

internal data class RandomRecipeUiState(
    val content: Content = Content.Loading,
    val canShowPrevious: Boolean = false,
) {
    sealed interface Content {
        data object Loading : Content
        data class Success(val recipe: Recipe) : Content
        data class Error(val error: DataError) : Content
    }
}

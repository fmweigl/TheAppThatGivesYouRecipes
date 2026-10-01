package com.example.yetanothermealsapp.randomrecipe.ui

import com.example.yetanothermealsapp.core.domain.DataError
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe

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

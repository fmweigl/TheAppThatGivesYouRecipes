package com.example.yetanothermealsapp.randomrecipe.ui

import com.example.yetanothermealsapp.core.domain.DataError
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe

internal sealed interface RandomRecipeUiState {
    data object Loading : RandomRecipeUiState
    data class Success(val recipe: Recipe) : RandomRecipeUiState
    data class Error(val error: DataError) : RandomRecipeUiState
}

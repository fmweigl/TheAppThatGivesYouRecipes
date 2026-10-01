package com.example.yetanothermealsapp.randomrecipe.ui

import com.example.yetanothermealsapp.randomrecipe.domain.Recipe

internal sealed interface RandomRecipeUiState {
    data object Loading : RandomRecipeUiState
    data class Success(val recipe: Recipe) : RandomRecipeUiState
    data object Error : RandomRecipeUiState
}

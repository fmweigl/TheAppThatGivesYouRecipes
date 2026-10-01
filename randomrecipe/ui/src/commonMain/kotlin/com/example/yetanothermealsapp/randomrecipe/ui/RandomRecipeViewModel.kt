package com.example.yetanothermealsapp.randomrecipe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yetanothermealsapp.core.domain.Result
import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class RandomRecipeViewModel(
    private val repository: RandomRecipeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RandomRecipeUiState>(RandomRecipeUiState.Loading)
    val uiState: StateFlow<RandomRecipeUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadRandomRecipe()
    }

    fun loadRandomRecipe() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = RandomRecipeUiState.Loading
            _uiState.value = when (val result = repository.getRandomRecipe()) {
                is Result.Success -> RandomRecipeUiState.Success(result.data)
                is Result.Failure -> RandomRecipeUiState.Error(result.error)
            }
        }
    }
}

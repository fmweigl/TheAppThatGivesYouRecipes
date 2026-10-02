package io.github.fmweigl.yetanothermealsapp.recipe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.RecipeRepository
import io.github.fmweigl.yetanothermealsapp.recipe.ui.RandomRecipeUiState.Content
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal const val MAX_HISTORY_SIZE = 50

/**
 * Shows random recipes and keeps the last [MAX_HISTORY_SIZE] in memory, so the user can step
 * back and forth through them without reloading.
 */
internal class RandomRecipeViewModel(
    private val repository: RecipeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RandomRecipeUiState())
    val uiState: StateFlow<RandomRecipeUiState> = _uiState.asStateFlow()

    private val history = mutableListOf<Recipe>()

    /** Position of the shown recipe in [history]. While loading or after an error it is [history]'s size. */
    private var index = 0

    private var loadJob: Job? = null

    init {
        loadNewRecipe()
    }

    /** Shows the next recipe in the history, or loads a new one when at its end. */
    fun showNext() {
        if (index < history.lastIndex) {
            showRecipeAt(index + 1)
        } else {
            loadNewRecipe()
        }
    }

    /** Shows the previous recipe in the history, cancelling a load in progress. */
    fun showPrevious() {
        if (index == 0) return
        loadJob?.cancel()
        showRecipeAt(index - 1)
    }

    private fun loadNewRecipe() {
        loadJob?.cancel()
        index = history.size
        show(Content.Loading)
        loadJob = viewModelScope.launch {
            when (val result = repository.getRandomRecipe()) {
                is Result.Success -> {
                    history += result.data
                    if (history.size > MAX_HISTORY_SIZE) history.removeAt(0)
                    showRecipeAt(history.lastIndex)
                }
                is Result.Failure -> show(Content.Error(result.error))
            }
        }
    }

    private fun showRecipeAt(newIndex: Int) {
        index = newIndex
        show(Content.Success(history[newIndex]))
    }

    private fun show(content: Content) {
        _uiState.value = RandomRecipeUiState(content = content, canShowPrevious = index > 0)
    }
}

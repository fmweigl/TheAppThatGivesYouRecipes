package io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe.RandomRecipeUiState.Content
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val MAX_HISTORY_SIZE = 50

/**
 * Shows random recipes and keeps the last [MAX_HISTORY_SIZE] in memory, so the user can step
 * back and forth through them without reloading. Whether the shown recipe is a favorite comes
 * from the database, so the heart also reflects changes made elsewhere (the favorites tab).
 */
internal class RandomRecipeViewModel(
    private val repository: RecipeRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RandomRecipeUiState())
    val uiState: StateFlow<RandomRecipeUiState> = _uiState.asStateFlow()

    private val history = mutableListOf<Recipe>()

    /** Position of the shown recipe in [history]. While loading or after an error it is [history]'s size. */
    private var index = 0

    private var loadJob: Job? = null

    private var favoriteJob: Job? = null

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

    /** Saves the shown recipe as a favorite, or removes it. A failed write leaves the heart unchanged. */
    fun toggleFavorite() {
        val state = _uiState.value
        val recipe = (state.content as? Content.Success)?.recipe ?: return
        viewModelScope.launch {
            if (state.isFavorite) {
                favoritesRepository.removeFavorite(recipe.id)
            } else {
                favoritesRepository.addFavorite(recipe)
            }
        }
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
        observeFavorite((content as? Content.Success)?.recipe?.id)
    }

    /** Keeps `isFavorite` up to date for the recipe with [recipeId] (false until the database answers). */
    private fun observeFavorite(recipeId: String?) {
        favoriteJob?.cancel()
        if (recipeId == null) return
        favoriteJob = viewModelScope.launch {
            favoritesRepository.observeIsFavorite(recipeId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }
}

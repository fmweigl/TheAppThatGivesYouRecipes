package io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail.RecipeUiState.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Shows the recipe with [recipeId]: a saved favorite from the database, any other from TheMealDB.
 * The recipe is loaded once, so it stays on screen when the user removes it from the favorites
 * here, and the heart can save it again.
 */
internal class RecipeViewModel(
    private val recipeId: String,
    private val recipeRepository: RecipeRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val content = MutableStateFlow<Content>(Content.Loading)

    val uiState: StateFlow<RecipeUiState> =
        combine(content, favoritesRepository.observeIsFavorite(recipeId), ::RecipeUiState)
            .stateIn(viewModelScope, SharingStarted.Eagerly, RecipeUiState())

    init {
        load()
    }

    fun retry() = load()

    /** Saves the recipe as a favorite, or removes it. A failed write leaves the heart unchanged. */
    fun toggleFavorite() {
        val recipe = (content.value as? Content.Success)?.recipe ?: return
        val isFavorite = uiState.value.isFavorite
        viewModelScope.launch {
            if (isFavorite) {
                favoritesRepository.removeFavorite(recipe.id)
            } else {
                favoritesRepository.addFavorite(recipe)
            }
        }
    }

    private fun load() {
        content.value = Content.Loading
        viewModelScope.launch {
            content.value = when (val result = recipeRepository.getRecipe(recipeId)) {
                is Result.Success -> Content.Success(result.data)
                is Result.Failure -> Content.Error(result.error)
            }
        }
    }
}

package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.favorites.ui.FavoritesUiState.Content
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The saved favorites, newest first. Removing one deletes it right away; until the "Removed"
 * message closes, "Undo" saves it again (as the newest favorite).
 */
internal class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    /** The favorites as last read, so a removed one can be saved again with all its data. */
    private var favorites: List<Recipe> = emptyList()

    private var removedRecipe: Recipe? = null

    private val removed = MutableStateFlow<RecipeTeaser?>(null)

    val uiState: StateFlow<FavoritesUiState> =
        combine(
            favoritesRepository.observeFavorites()
                .onEach { result -> if (result is Result.Success) favorites = result.data }
                .map { result -> result.toContent() },
            removed,
            ::FavoritesUiState,
        ).stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesUiState())

    /** Deletes the favorite; a failed delete leaves it in the list. */
    fun remove(recipeId: String) {
        val recipe = favorites.find { it.id == recipeId } ?: return
        viewModelScope.launch {
            if (favoritesRepository.removeFavorite(recipeId) is Result.Success) {
                removedRecipe = recipe
                removed.value = recipe.toTeaser()
            }
        }
    }

    /** The "Removed" message closed; [undo] if the user chose "Undo". */
    fun removalMessageClosed(undo: Boolean) {
        val recipe = removedRecipe
        removedRecipe = null
        removed.value = null
        if (undo && recipe != null) {
            viewModelScope.launch { favoritesRepository.addFavorite(recipe) }
        }
    }
}

private fun Result<List<Recipe>, *>.toContent(): Content = when (this) {
    is Result.Success -> if (data.isEmpty()) Content.Empty else Content.Favorites(data.map { it.toTeaser() })
    is Result.Failure -> Content.Error
}

private fun Recipe.toTeaser() = RecipeTeaser(
    id = id,
    name = name,
    subtitle = listOfNotNull(category, area).joinToString(" · ").ifEmpty { null },
    imageUrl = imageUrl,
)

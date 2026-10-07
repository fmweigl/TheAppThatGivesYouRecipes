package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.favorites.ui.FavoritesUiState.Content
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.RemovedFavorite
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The saved favorites, newest first. Removing one deletes it right away; until the "Removed"
 * message closes, "Undo" restores it with its original save time, so it returns to its old position.
 */
internal class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private var removedFavorite: RemovedFavorite? = null

    private val removed = MutableStateFlow<RecipeTeaser?>(null)

    private val restoreFailed = MutableStateFlow<RecipeTeaser?>(null)

    val uiState: StateFlow<FavoritesUiState> =
        combine(
            favoritesRepository.observeFavorites()
                .map { result -> result.toContent() },
            removed,
            restoreFailed,
            ::FavoritesUiState,
        ).stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesUiState())

    /** Deletes the favorite; a failed delete leaves it in the list. */
    fun remove(recipeId: String) {
        viewModelScope.launch {
            val result = favoritesRepository.removeFavorite(recipeId)
            if (result is Result.Success) {
                removedFavorite = result.data
                removed.value = result.data.recipe.toTeaser()
            }
        }
    }

    /** The "Removed" message closed; [undo] if the user chose "Undo". */
    fun removalMessageClosed(undo: Boolean) {
        val favorite = removedFavorite
        removedFavorite = null
        removed.value = null
        if (undo && favorite != null) {
            viewModelScope.launch {
                if (favoritesRepository.restoreFavorite(favorite) is Result.Failure) {
                    restoreFailed.value = favorite.recipe.toTeaser()
                }
            }
        }
    }

    /** The "Couldn't restore" message closed. */
    fun restoreFailureMessageClosed() {
        restoreFailed.value = null
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

package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.favorites.ui.FavoritesUiState.Content
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.RemovedFavorite
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The saved favorites, newest first. Removing one deletes it after [REMOVE_DELAY_MILLIS]; until the
 * "Removed" message closes, "Undo" restores it with its original save time, so it returns to its old
 * position.
 */
internal class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private var removedFavorite: RemovedFavorite? = null

    private val removed = MutableStateFlow<RecipeTeaser?>(null)

    private val restoreFailed = MutableStateFlow<RecipeTeaser?>(null)

    private val removing = MutableStateFlow<Set<String>>(emptySet())

    private val removeFailed = MutableStateFlow<RecipeTeaser?>(null)

    val uiState: StateFlow<FavoritesUiState> =
        combine(
            favoritesRepository.observeFavorites()
                .map { result -> result.toContent() },
            removed,
            restoreFailed,
            removing,
            removeFailed,
            ::FavoritesUiState,
        ).stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesUiState())

    /**
     * Marks the favorite as being removed (its heart empties), then deletes it. It stays marked until
     * the list no longer shows it, so its heart doesn't fill again just before the card goes. A
     * failed delete leaves it in the list, unmarked, and is reported until its message closes.
     */
    fun remove(recipeId: String) {
        if (recipeId in removing.value) return
        val teaser = uiState.value.content.teasers().find { it.id == recipeId } ?: return
        removing.update { it + recipeId }
        removeFailed.value = null
        viewModelScope.launch {
            delay(REMOVE_DELAY_MILLIS)
            when (val result = favoritesRepository.removeFavorite(recipeId)) {
                is Result.Success -> {
                    restoreFailed.value = null
                    removedFavorite = result.data
                    removed.value = result.data.recipe.toTeaser()
                    uiState.first { state -> state.content.teasers().none { it.id == recipeId } }
                }
                is Result.Failure -> removeFailed.value = teaser
            }
            removing.update { it - recipeId }
        }
    }

    /** The "Couldn't remove" message closed. */
    fun removeFailureMessageClosed() {
        removeFailed.value = null
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

/** Time for the heart's emptying animation to play before the card disappears. */
internal const val REMOVE_DELAY_MILLIS = 300L

private fun Content.teasers(): List<RecipeTeaser> = (this as? Content.Favorites)?.teasers.orEmpty()

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

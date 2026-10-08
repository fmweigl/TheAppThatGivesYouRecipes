package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.RemovedFavorite
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

/**
 * Keeps the favorites newest first, each with its save time. Reading fails while [readFails] is
 * set (when the flow is collected), writing while [writeError] is set, restoring while
 * [restoreError] is set.
 */
internal class FakeFavoritesRepository : FavoritesRepository {
    private class Entry(val recipe: Recipe, val savedAt: Instant)

    private val entries = MutableStateFlow<List<Entry>>(emptyList())
    private var clock = 0L

    /** The favorites, newest first; setting them gives them save times in that order. */
    var favorites: List<Recipe>
        get() = entries.value.map { it.recipe }
        set(value) {
            val base = clock
            clock += value.size
            entries.value = value.mapIndexed { index, recipe ->
                Entry(recipe, Instant.fromEpochMilliseconds(base + value.size - index))
            }
        }

    var readFails = false
    var writeError: DataError? = null
    var restoreError: DataError? = null

    override fun observeFavorites(): Flow<Result<List<Recipe>, DataError>> =
        if (readFails) {
            flowOf(Result.Failure(DataError.Storage))
        } else {
            entries.map { list -> Result.Success(list.map { it.recipe }) }
        }

    override fun observeIsFavorite(recipeId: String): Flow<Boolean> =
        entries.map { list -> list.any { it.recipe.id == recipeId } }.distinctUntilChanged()

    override suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError> = write {
        val entry = Entry(recipe, Instant.fromEpochMilliseconds(++clock))
        entries.update { list -> listOf(entry) + list.filter { it.recipe.id != recipe.id } }
    }

    override suspend fun removeFavorite(recipeId: String): Result<RemovedFavorite, DataError> {
        val entry = entries.value.find { it.recipe.id == recipeId }
        if (writeError != null || entry == null) return Result.Failure(writeError ?: DataError.NotFound)
        entries.update { list -> list.filter { it.recipe.id != recipeId } }
        return Result.Success(RemovedFavorite(entry.recipe, entry.savedAt))
    }

    override suspend fun restoreFavorite(removed: RemovedFavorite): Result<Unit, DataError> {
        restoreError?.let { return Result.Failure(it) }
        return write {
            entries.update { list ->
                if (list.any { it.recipe.id == removed.recipe.id }) {
                    list
                } else {
                    (list + Entry(removed.recipe, removed.savedAt)).sortedByDescending { it.savedAt }
                }
            }
        }
    }

    private fun write(change: () -> Unit): Result<Unit, DataError> {
        writeError?.let { return Result.Failure(it) }
        change()
        return Result.Success(Unit)
    }
}

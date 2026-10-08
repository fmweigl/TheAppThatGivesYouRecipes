package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.RemovedFavorite
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

/** Keeps the favorites in [favorites], newest first; writes fail with [writeError] while it is set. */
internal class FakeFavoritesRepository : FavoritesRepository {
    val favorites = MutableStateFlow<List<Recipe>>(emptyList())
    var writeError: DataError? = null

    override fun observeFavorites(): Flow<Result<List<Recipe>, DataError>> = favorites.map { Result.Success(it) }

    override fun observeIsFavorite(recipeId: String): Flow<Boolean> =
        favorites.map { recipes -> recipes.any { it.id == recipeId } }.distinctUntilChanged()

    override suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError> = write {
        favorites.update { recipes -> listOf(recipe) + recipes.filter { it.id != recipe.id } }
    }

    override suspend fun removeFavorite(recipeId: String): Result<RemovedFavorite, DataError> {
        val recipe = favorites.value.find { it.id == recipeId }
        if (writeError != null || recipe == null) return Result.Failure(writeError ?: DataError.NotFound)
        favorites.update { recipes -> recipes.filter { it.id != recipeId } }
        return Result.Success(RemovedFavorite(recipe, Instant.fromEpochMilliseconds(0)))
    }

    /** Simple: no recipe screen restores favorites, so the position doesn't matter. */
    override suspend fun restoreFavorite(removed: RemovedFavorite): Result<Unit, DataError> = write {
        favorites.update { recipes ->
            if (recipes.any { it.id == removed.recipe.id }) recipes else recipes + removed.recipe
        }
    }

    private fun write(change: () -> Unit): Result<Unit, DataError> {
        writeError?.let { return Result.Failure(it) }
        change()
        return Result.Success(Unit)
    }
}

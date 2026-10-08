package io.github.fmweigl.theappthatgivesyourecipes

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

/** Favorites in memory, starting with [initial] (newest first), so screenshots never touch the real database. */
internal class ScreenshotFavoritesRepository(initial: List<Recipe>) : FavoritesRepository {

    private val favorites = MutableStateFlow(initial)

    /** Save times, newest favorite largest, so a removed favorite returns to its old position. */
    private val savedAt = initial
        .withIndex()
        .associate { (index, recipe) -> recipe.id to (initial.size - index).toLong() }
        .toMutableMap()
    private var clock = initial.size.toLong()

    override fun observeFavorites(): Flow<Result<List<Recipe>, DataError>> = favorites.map { Result.Success(it) }

    override fun observeIsFavorite(recipeId: String): Flow<Boolean> =
        favorites.map { recipes -> recipes.any { it.id == recipeId } }.distinctUntilChanged()

    override suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError> {
        savedAt[recipe.id] = ++clock
        favorites.update { recipes -> listOf(recipe) + recipes.filter { it.id != recipe.id } }
        return Result.Success(Unit)
    }

    override suspend fun removeFavorite(recipeId: String): Result<RemovedFavorite, DataError> {
        val recipe = favorites.value.find { it.id == recipeId } ?: return Result.Failure(DataError.NotFound)
        favorites.update { recipes -> recipes.filter { it.id != recipeId } }
        return Result.Success(RemovedFavorite(recipe, Instant.fromEpochMilliseconds(savedAt.getValue(recipeId))))
    }

    override suspend fun restoreFavorite(removed: RemovedFavorite): Result<Unit, DataError> {
        if (favorites.value.any { it.id == removed.recipe.id }) return Result.Success(Unit)
        savedAt[removed.recipe.id] = removed.savedAt.toEpochMilliseconds()
        favorites.update { recipes -> (recipes + removed.recipe).sortedByDescending { savedAt.getValue(it.id) } }
        return Result.Success(Unit)
    }
}

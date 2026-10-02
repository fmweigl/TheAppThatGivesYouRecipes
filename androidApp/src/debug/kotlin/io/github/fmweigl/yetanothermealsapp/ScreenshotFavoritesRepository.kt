package io.github.fmweigl.yetanothermealsapp

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Favorites in memory, starting with [initial] (newest first), so screenshots never touch the real database. */
internal class ScreenshotFavoritesRepository(initial: List<Recipe>) : FavoritesRepository {

    private val favorites = MutableStateFlow(initial)

    override fun observeFavorites(): Flow<Result<List<Recipe>, DataError>> = favorites.map { Result.Success(it) }

    override fun observeIsFavorite(recipeId: String): Flow<Boolean> =
        favorites.map { recipes -> recipes.any { it.id == recipeId } }.distinctUntilChanged()

    override suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError> {
        favorites.update { recipes -> listOf(recipe) + recipes.filter { it.id != recipe.id } }
        return Result.Success(Unit)
    }

    override suspend fun removeFavorite(recipeId: String): Result<Unit, DataError> {
        favorites.update { recipes -> recipes.filter { it.id != recipeId } }
        return Result.Success(Unit)
    }
}

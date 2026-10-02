package io.github.fmweigl.yetanothermealsapp.favorites.ui

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Keeps the favorites in [favorites], newest first. Reading fails while [readFails] is set
 * (when the flow is collected), writing while [writeError] is set.
 */
internal class FakeFavoritesRepository : FavoritesRepository {
    val favorites = MutableStateFlow<List<Recipe>>(emptyList())
    var readFails = false
    var writeError: DataError? = null

    override fun observeFavorites(): Flow<Result<List<Recipe>, DataError>> =
        if (readFails) flowOf(Result.Failure(DataError.Storage)) else favorites.map { Result.Success(it) }

    override fun observeIsFavorite(recipeId: String): Flow<Boolean> =
        favorites.map { recipes -> recipes.any { it.id == recipeId } }.distinctUntilChanged()

    override suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError> = write {
        favorites.update { recipes -> listOf(recipe) + recipes.filter { it.id != recipe.id } }
    }

    override suspend fun removeFavorite(recipeId: String): Result<Unit, DataError> = write {
        favorites.update { recipes -> recipes.filter { it.id != recipeId } }
    }

    private fun write(change: () -> Unit): Result<Unit, DataError> {
        writeError?.let { return Result.Failure(it) }
        change()
        return Result.Success(Unit)
    }
}

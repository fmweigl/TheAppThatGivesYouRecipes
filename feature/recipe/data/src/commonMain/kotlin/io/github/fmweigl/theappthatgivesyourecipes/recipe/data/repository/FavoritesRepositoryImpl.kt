package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import androidx.sqlite.SQLiteException
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.FavoriteRecipeDao
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.safeDbCall
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.toFavoriteRecipeEntity
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.toRecipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.RemovedFavorite
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.Instant

internal class FavoritesRepositoryImpl(
    private val dao: FavoriteRecipeDao,
    private val clock: Clock = Clock.System,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<Result<List<Recipe>, DataError>> =
        dao.observeAll()
            .map<_, Result<List<Recipe>, DataError>> { entities -> Result.Success(entities.map { it.toRecipe() }) }
            .catch { error -> if (error is SQLiteException) emit(Result.Failure(DataError.Storage)) else throw error }

    override fun observeIsFavorite(recipeId: String): Flow<Boolean> =
        dao.observeExists(recipeId)
            .catch { error -> if (error is SQLiteException) emit(false) else throw error }

    override suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError> = safeDbCall {
        dao.upsert(recipe.toFavoriteRecipeEntity(savedAt = clock.now().toEpochMilliseconds()))
    }

    override suspend fun removeFavorite(recipeId: String): Result<RemovedFavorite, DataError> {
        val result = safeDbCall { dao.deleteAndGet(recipeId) }
        return when (result) {
            is Result.Success -> result.data
                ?.let { Result.Success(RemovedFavorite(it.toRecipe(), Instant.fromEpochMilliseconds(it.savedAt))) }
                ?: Result.Failure(DataError.NotFound)
            is Result.Failure -> result
        }
    }

    override suspend fun restoreFavorite(removed: RemovedFavorite): Result<Unit, DataError> = safeDbCall {
        dao.insertIfAbsent(removed.recipe.toFavoriteRecipeEntity(savedAt = removed.savedAt.toEpochMilliseconds()))
    }
}

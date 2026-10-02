package io.github.fmweigl.yetanothermealsapp.recipe.data.repository

import androidx.sqlite.SQLiteException
import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.FavoriteRecipeDao
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.safeDbCall
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.toFavoriteRecipeEntity
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.toRecipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

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

    override suspend fun removeFavorite(recipeId: String): Result<Unit, DataError> = safeDbCall {
        dao.delete(recipeId)
    }
}

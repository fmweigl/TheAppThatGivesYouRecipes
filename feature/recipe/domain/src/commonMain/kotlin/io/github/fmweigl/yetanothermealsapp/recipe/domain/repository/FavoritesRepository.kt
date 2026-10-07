package io.github.fmweigl.yetanothermealsapp.recipe.domain.repository

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/** The recipes the user saved as favorites, stored on the device. */
interface FavoritesRepository {
    /** The favorites, most recently saved first; emits again whenever they change. */
    fun observeFavorites(): Flow<Result<List<Recipe>, DataError>>

    /** Whether the recipe with [recipeId] is a favorite; emits again when that changes, `false` if it can't be read. */
    fun observeIsFavorite(recipeId: String): Flow<Boolean>

    /** Saves [recipe] as a favorite, or updates it if it already is one. */
    suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError>

    suspend fun removeFavorite(recipeId: String): Result<Unit, DataError>
}

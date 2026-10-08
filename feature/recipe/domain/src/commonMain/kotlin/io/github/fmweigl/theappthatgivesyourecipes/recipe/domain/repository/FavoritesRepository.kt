package io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.RemovedFavorite
import kotlinx.coroutines.flow.Flow

/** The recipes the user saved as favorites, stored on the device. */
interface FavoritesRepository {
    /** The favorites, most recently saved first; emits again whenever they change. */
    fun observeFavorites(): Flow<Result<List<Recipe>, DataError>>

    /** Whether the recipe with [recipeId] is a favorite; emits again when that changes, `false` if it can't be read. */
    fun observeIsFavorite(recipeId: String): Flow<Boolean>

    /** Saves [recipe] as a favorite, or updates it if it already is one. */
    suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError>

    /**
     * Removes the favorite and returns it for [restoreFavorite];
     * [DataError.NotFound] if the recipe is no favorite (callers that only toggle can ignore it).
     */
    suspend fun removeFavorite(recipeId: String): Result<RemovedFavorite, DataError>

    /**
     * Saves a [removed] favorite again with its original save time, so it returns to its old position.
     * Does nothing (and succeeds) if the recipe was added as a favorite again in the meantime.
     */
    suspend fun restoreFavorite(removed: RemovedFavorite): Result<Unit, DataError>
}

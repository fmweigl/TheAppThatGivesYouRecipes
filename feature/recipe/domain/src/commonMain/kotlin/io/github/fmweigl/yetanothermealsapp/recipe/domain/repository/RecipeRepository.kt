package io.github.fmweigl.yetanothermealsapp.recipe.domain.repository

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>

    /** The recipe with this TheMealDB [id]; fails with [DataError.NotFound] if there is none. */
    suspend fun getRecipe(id: String): Result<Recipe, DataError>
}

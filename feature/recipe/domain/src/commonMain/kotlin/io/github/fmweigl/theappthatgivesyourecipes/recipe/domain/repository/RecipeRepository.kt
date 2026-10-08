package io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>

    /** The recipe with this TheMealDB [id]; fails with [DataError.NotFound] if there is none. */
    suspend fun getRecipe(id: String): Result<Recipe, DataError>
}

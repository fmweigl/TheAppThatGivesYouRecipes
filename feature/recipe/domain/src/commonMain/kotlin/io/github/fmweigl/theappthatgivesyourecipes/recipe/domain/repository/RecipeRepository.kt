package io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>

    /** The recipe with this TheMealDB [id]; fails with [DataError.NotFound] if there is none. */
    suspend fun getRecipe(id: String): Result<Recipe, DataError>

    /**
     * TheMealDB's recipes whose name contains [query] (ignoring case and accents), in the order the
     * API returns them: at most 25, shortest names first. No match is an empty list, not an error.
     * The results are complete, so [getRecipe] opens them without another request.
     */
    suspend fun searchRecipes(query: String): Result<List<Recipe>, DataError>
}

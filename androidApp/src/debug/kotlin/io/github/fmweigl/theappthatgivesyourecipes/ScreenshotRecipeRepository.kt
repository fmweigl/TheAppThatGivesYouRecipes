package io.github.fmweigl.theappthatgivesyourecipes

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.RecipeRepository

/** Returns [recipes] in turn, starting over after the last one, and looks them up by id. */
internal class ScreenshotRecipeRepository(private val recipes: List<Recipe>) : RecipeRepository {

    private var next = 0

    override suspend fun getRandomRecipe(): Result<Recipe, DataError> =
        Result.Success(recipes[next++ % recipes.size])

    override suspend fun getRecipe(id: String): Result<Recipe, DataError> =
        recipes.find { it.id == id }?.let { Result.Success(it) } ?: Result.Failure(DataError.NotFound)

    override suspend fun searchRecipes(query: String): Result<List<Recipe>, DataError> =
        Result.Success(recipes.filter { it.name.contains(query, ignoreCase = true) })
}

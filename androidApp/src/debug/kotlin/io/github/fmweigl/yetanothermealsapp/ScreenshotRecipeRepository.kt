package io.github.fmweigl.yetanothermealsapp

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository

/** Returns [recipes] in turn, starting over after the last one. */
internal class ScreenshotRecipeRepository(private val recipes: List<Recipe>) : RecipeRepository {

    private var next = 0

    override suspend fun getRandomRecipe(): Result<Recipe, DataError> =
        Result.Success(recipes[next++ % recipes.size])
}

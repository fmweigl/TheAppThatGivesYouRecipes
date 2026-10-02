package io.github.fmweigl.yetanothermealsapp.recipe.domain

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result

interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>
}

package io.github.fmweigl.yetanothermealsapp.recipe.domain.repository

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>
}

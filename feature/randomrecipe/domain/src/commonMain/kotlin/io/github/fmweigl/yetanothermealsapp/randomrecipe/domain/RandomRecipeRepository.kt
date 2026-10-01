package io.github.fmweigl.yetanothermealsapp.randomrecipe.domain

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result

interface RandomRecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>
}

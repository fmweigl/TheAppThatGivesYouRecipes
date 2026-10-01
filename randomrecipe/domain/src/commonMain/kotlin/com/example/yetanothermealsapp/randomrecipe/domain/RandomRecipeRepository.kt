package com.example.yetanothermealsapp.randomrecipe.domain

import com.example.yetanothermealsapp.core.domain.DataError
import com.example.yetanothermealsapp.core.domain.Result

interface RandomRecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>
}

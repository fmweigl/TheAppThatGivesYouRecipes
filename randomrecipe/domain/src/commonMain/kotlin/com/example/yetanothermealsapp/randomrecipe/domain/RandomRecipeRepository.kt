package com.example.yetanothermealsapp.randomrecipe.domain

interface RandomRecipeRepository {
    suspend fun getRandomRecipe(): Recipe
}

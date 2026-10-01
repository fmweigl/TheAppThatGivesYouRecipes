package com.example.yetanothermealsapp.randomrecipe.domain

interface RandomRecipeRepository {
    /** Throws if the recipe cannot be loaded. */
    suspend fun getRandomRecipe(): Recipe
}

package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe

class RandomRecipeRepositoryImpl : RandomRecipeRepository {
    override suspend fun getRandomRecipe(): Recipe = TODO("Not yet implemented")
}

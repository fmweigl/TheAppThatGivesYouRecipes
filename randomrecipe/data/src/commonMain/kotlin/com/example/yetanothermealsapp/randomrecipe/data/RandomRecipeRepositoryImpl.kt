package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/** [client] must be created with [createTheMealDbHttpClient]. */
internal class RandomRecipeRepositoryImpl(
    private val client: HttpClient,
) : RandomRecipeRepository {

    override suspend fun getRandomRecipe(): Recipe {
        val response: MealsResponse = client.get("random.php").body()
        val meal = response.meals?.firstOrNull() ?: throw NoSuchElementException("TheMealDB returned no meal")
        return meal.toRecipe()
    }
}

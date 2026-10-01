package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.core.data.safeApiCall
import com.example.yetanothermealsapp.core.domain.DataError
import com.example.yetanothermealsapp.core.domain.Result
import com.example.yetanothermealsapp.core.domain.flatMap
import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/** [client] must be created with [createTheMealDbHttpClient]. */
internal class RandomRecipeRepositoryImpl(
    private val client: HttpClient,
) : RandomRecipeRepository {

    override suspend fun getRandomRecipe(): Result<Recipe, DataError> =
        safeApiCall { client.get("random.php").body<MealsResponse>() }
            .flatMap { response ->
                // random.php always returns a meal, so a missing or unmappable one means a broken response.
                val recipe = response.meals?.firstOrNull()?.toRecipe()
                if (recipe != null) Result.Success(recipe) else Result.Failure(DataError.InvalidResponse)
            }
}

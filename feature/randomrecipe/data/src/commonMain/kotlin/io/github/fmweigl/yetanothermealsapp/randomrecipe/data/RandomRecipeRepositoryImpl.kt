package io.github.fmweigl.yetanothermealsapp.randomrecipe.data

import io.github.fmweigl.yetanothermealsapp.core.data.safeApiCall
import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.core.domain.flatMap
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

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

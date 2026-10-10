package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import io.github.fmweigl.theappthatgivesyourecipes.core.data.safeApiCall
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.flatMap
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.map
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.FavoriteRecipeDao
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.safeDbCall
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.toRecipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.model.MealsResponse
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.model.toRecipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.RecipeRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class RecipeRepositoryImpl(
    private val client: HttpClient,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val recipeCache: RecipeCache,
) : RecipeRepository {

    override suspend fun getRandomRecipe(): Result<Recipe, DataError> =
        safeApiCall { client.get("random.php").body<MealsResponse>() }
            .flatMap { response ->
                // random.php always returns a meal, so a missing or unmappable one means a broken response.
                val recipe = response.meals.firstOrNull()?.toRecipe()
                if (recipe != null) Result.Success(recipe) else Result.Failure(DataError.InvalidResponse)
            }

    /**
     * Loads a recipe from the cheapest source that has it, in this order:
     * 1. The favorites database: a saved favorite is stored completely, so it opens offline. If the
     *    database can't be read, this step is skipped.
     * 2. The [RecipeCache]: recipes from earlier search results, in memory, so opening a result
     *    needs no request.
     * 3. The network: `lookup.php` on TheMealDB, for anything neither of the others knows.
     * The database comes first so a favorite is never replaced by a possibly older cached copy.
     */
    override suspend fun getRecipe(id: String): Result<Recipe, DataError> {
        val saved = when (val result = safeDbCall { favoriteRecipeDao.getById(id) }) {
            is Result.Success -> result.data
            // TheMealDB still has the recipe if the database can't be read.
            is Result.Failure -> null
        }
        if (saved != null) return Result.Success(saved.toRecipe())
        return recipeCache[id]?.let { Result.Success(it) } ?: lookUpRecipe(id)
    }

    override suspend fun searchRecipes(query: String): Result<List<Recipe>, DataError> =
        safeApiCall { client.get("search.php") { parameter("s", query) }.body<MealsResponse>() }
            .map { response ->
                // Meals that can't be mapped are skipped; none at all is simply no match.
                response.meals.mapNotNull { it.toRecipe() }.also(recipeCache::putAll)
            }

    private suspend fun lookUpRecipe(id: String): Result<Recipe, DataError> =
        safeApiCall { client.get("lookup.php") { parameter("i", id) }.body<MealsResponse>() }
            .flatMap { response ->
                val meal = response.meals.firstOrNull()
                val recipe = meal?.toRecipe()
                when {
                    meal == null -> Result.Failure(DataError.NotFound)
                    recipe == null -> Result.Failure(DataError.InvalidResponse)
                    else -> Result.Success(recipe)
                }
            }
}

package io.github.fmweigl.yetanothermealsapp.randomrecipe.data

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.core.network.createTheMealDbHttpClient
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Ingredient
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Recipe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class RandomRecipeRepositoryImplTest {

    private fun repository(handler: MockRequestHandler): RandomRecipeRepositoryImpl {
        val engine = MockEngine { request ->
            assertEquals("https://www.themealdb.com/api/json/v2/1/random.php", request.url.toString())
            handler(request)
        }
        return RandomRecipeRepositoryImpl(createTheMealDbHttpClient(engine))
    }

    private fun repository(body: String, status: HttpStatusCode = HttpStatusCode.OK) = repository {
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    private suspend fun RandomRecipeRepositoryImpl.loadRecipe(): Recipe =
        assertIs<Result.Success<Recipe>>(getRandomRecipe()).data

    @Test
    fun mapsMealFields() = runTest {
        val recipe = repository(MEAL_JSON).loadRecipe()

        assertEquals("52923", recipe.id)
        assertEquals("Canadian Butter Tarts", recipe.name)
        assertEquals("Dessert", recipe.category)
        assertEquals("Canadian", recipe.area)
        assertEquals("Preheat the oven.\r\nBake.", recipe.instructions)
        assertEquals("https://www.themealdb.com/images/media/meals/wpputp1511812960.jpg", recipe.imageUrl)
        assertEquals("https://www.youtube.com/watch?v=WUpaOGghOdo", recipe.youtubeUrl)
        assertNull(recipe.sourceUrl)
        assertEquals(listOf("Speciality", "Snack"), recipe.tags)
    }

    @Test
    fun pairsIngredientsWithMeasuresInOrderAndSkipsEmptySlots() = runTest {
        val recipe = repository(MEAL_JSON).loadRecipe()

        assertEquals(
            listOf(
                Ingredient("Shortcrust Pastry", "375g"),
                Ingredient("Eggs", ""),
                Ingredient("Raisins", "100g"),
            ),
            recipe.ingredients,
        )
    }

    @Test
    fun failsWithInvalidResponseWhenNoMealIsReturned() = runTest {
        assertEquals(
            Result.Failure(DataError.InvalidResponse),
            repository("""{"meals":null}""").getRandomRecipe(),
        )
    }

    @Test
    fun failsWithInvalidResponseWhenMealHasNoId() = runTest {
        assertEquals(
            Result.Failure(DataError.InvalidResponse),
            repository("""{"meals":[{"strMeal":"Soup"}]}""").getRandomRecipe(),
        )
    }

    @Test
    fun failsWithInvalidResponseOnMalformedJson() = runTest {
        assertEquals(Result.Failure(DataError.InvalidResponse), repository("""{"meals":[""").getRandomRecipe())
    }

    @Test
    fun failsWithServerOnServerError() = runTest {
        assertEquals(
            Result.Failure(DataError.Server),
            repository("", HttpStatusCode.InternalServerError).getRandomRecipe(),
        )
    }

    @Test
    fun failsWithNoConnectionOnNetworkError() = runTest {
        assertEquals(
            Result.Failure(DataError.NoConnection),
            repository { throw IOException("offline") }.getRandomRecipe(),
        )
    }

    private companion object {
        val MEAL_JSON = """
            {"meals":[{
              "idMeal":"52923",
              "strMeal":"Canadian Butter Tarts",
              "strCategory":"Dessert",
              "strArea":"Canadian",
              "strInstructions":"Preheat the oven.\r\nBake.",
              "strMealThumb":"https:\/\/www.themealdb.com\/images\/media\/meals\/wpputp1511812960.jpg",
              "strTags":"Speciality, Snack,",
              "strYoutube":"https:\/\/www.youtube.com\/watch?v=WUpaOGghOdo",
              "strIngredient1":"Shortcrust Pastry", "strMeasure1":"375g",
              "strIngredient2":"Eggs", "strMeasure2":" ",
              "strIngredient3":"", "strMeasure3":"",
              "strIngredient4":" Raisins ", "strMeasure4":"100g ",
              "strIngredient5":null, "strMeasure5":null,
              "strSource":""
            }]}
        """.trimIndent()
    }
}

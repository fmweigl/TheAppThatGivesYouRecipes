package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.randomrecipe.domain.Ingredient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RandomRecipeRepositoryImplTest {

    private fun repository(body: String, status: HttpStatusCode = HttpStatusCode.OK): RandomRecipeRepositoryImpl {
        val engine = MockEngine { request ->
            assertEquals("https://www.themealdb.com/api/json/v1/1/random.php", request.url.toString())
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        return RandomRecipeRepositoryImpl(HttpClient(engine) { theMealDbConfig() })
    }

    @Test
    fun mapsMealFields() = runTest {
        val recipe = repository(MEAL_JSON).getRandomRecipe()

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
        val recipe = repository(MEAL_JSON).getRandomRecipe()

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
    fun failsWhenNoMealIsReturned() = runTest {
        assertFailsWith<NoSuchElementException> {
            repository("""{"meals":null}""").getRandomRecipe()
        }
    }

    @Test
    fun failsOnServerError() = runTest {
        assertFailsWith<ServerResponseException> {
            repository("", HttpStatusCode.InternalServerError).getRandomRecipe()
        }
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

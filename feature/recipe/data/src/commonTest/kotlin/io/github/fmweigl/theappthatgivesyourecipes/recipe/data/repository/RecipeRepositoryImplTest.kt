package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.core.network.createTheMealDbHttpClient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.inMemoryRecipeDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.testRecipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.toFavoriteRecipeEntity
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.fail

class RecipeRepositoryImplTest {

    private val database = inMemoryRecipeDatabase()

    @AfterTest
    fun closeDatabase() = database.close()

    private fun repository(expectedUrl: String = RANDOM_URL, handler: MockRequestHandler): RecipeRepositoryImpl {
        val engine = MockEngine { request ->
            assertEquals(expectedUrl, request.url.toString())
            handler(request)
        }
        return RecipeRepositoryImpl(createTheMealDbHttpClient(engine), database.favoriteRecipeDao(), RecipeCache())
    }

    private fun repository(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        expectedUrl: String = RANDOM_URL,
    ) = repository(expectedUrl) {
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    private suspend fun RecipeRepositoryImpl.loadRecipe(): Recipe =
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
    fun failsWithInvalidResponseWhenMealsIsAMessage() = runTest {
        assertEquals(
            Result.Failure(DataError.InvalidResponse),
            repository("""{"meals":"no data found"}""").getRandomRecipe(),
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

    @Test
    fun getRecipeLooksUpTheIdAndMapsTheMeal() = runTest {
        val result = repository(MEAL_JSON, expectedUrl = "$LOOKUP_URL?i=52923").getRecipe("52923")

        val recipe = assertIs<Result.Success<Recipe>>(result).data
        assertEquals("52923", recipe.id)
        assertEquals("Canadian Butter Tarts", recipe.name)
        assertEquals(3, recipe.ingredients.size)
    }

    @Test
    fun getRecipeReturnsASavedFavoriteWithoutARequest() = runTest {
        val saved = testRecipe("52923")
        database.favoriteRecipeDao().upsert(saved.toFavoriteRecipeEntity(savedAt = 1))
        val repository = repository(expectedUrl = "none") { fail("A saved recipe needs no request") }

        assertEquals(Result.Success(saved), repository.getRecipe("52923"))
    }

    @Test
    fun getRecipeLooksUpRecipesThatAreNotSaved() = runTest {
        database.favoriteRecipeDao().upsert(testRecipe("1").toFavoriteRecipeEntity(savedAt = 1))

        val result = repository(MEAL_JSON, expectedUrl = "$LOOKUP_URL?i=52923").getRecipe("52923")

        assertEquals("Canadian Butter Tarts", assertIs<Result.Success<Recipe>>(result).data.name)
    }

    @Test
    fun getRecipeFailsWithNotFoundWhenNoMealIsReturned() = runTest {
        assertEquals(
            Result.Failure(DataError.NotFound),
            repository("""{"meals":null}""", expectedUrl = "$LOOKUP_URL?i=1").getRecipe("1"),
        )
    }

    @Test
    fun getRecipeFailsWithNotFoundWhenTheIdIsInvalid() = runTest {
        assertEquals(
            Result.Failure(DataError.NotFound),
            repository("""{"meals":"Invalid ID"}""", expectedUrl = "$LOOKUP_URL?i=abc").getRecipe("abc"),
        )
    }

    @Test
    fun getRecipeFailsWithInvalidResponseWhenMealHasNoId() = runTest {
        assertEquals(
            Result.Failure(DataError.InvalidResponse),
            repository("""{"meals":[{"strMeal":"Soup"}]}""", expectedUrl = "$LOOKUP_URL?i=1").getRecipe("1"),
        )
    }

    @Test
    fun getRecipeFailsWithNoConnectionOnNetworkError() = runTest {
        assertEquals(
            Result.Failure(DataError.NoConnection),
            repository("$LOOKUP_URL?i=1") { throw IOException("offline") }.getRecipe("1"),
        )
    }

    @Test
    fun searchRecipesSendsTheQueryAndMapsTheMeals() = runTest {
        val result = repository(MEAL_JSON, expectedUrl = "$SEARCH_URL?s=tart").searchRecipes("tart")

        val recipes = assertIs<Result.Success<List<Recipe>>>(result).data
        assertEquals(listOf("52923"), recipes.map { it.id })
        assertEquals(3, recipes.single().ingredients.size)
    }

    @Test
    fun searchRecipesReturnsAnEmptyListWhenThereIsNoMatch() = runTest {
        assertEquals(
            Result.Success(emptyList<Recipe>()),
            repository("""{"meals":null}""", expectedUrl = "$SEARCH_URL?s=zzz").searchRecipes("zzz"),
        )
    }

    @Test
    fun searchRecipesTrimsTheNames() = runTest {
        val body = """{"meals":[{"idMeal":"1","strMeal":"  Pad Thai "},{"idMeal":"2","strMeal":"Pad Thai Soup"}]}"""

        val result = repository(body, expectedUrl = "$SEARCH_URL?s=pad+thai").searchRecipes("pad thai")

        assertEquals(
            listOf("Pad Thai", "Pad Thai Soup"),
            assertIs<Result.Success<List<Recipe>>>(result).data.map { it.name },
        )
    }

    @Test
    fun searchRecipesKeepsTheOrderOfTheResponse() = runTest {
        val body = """{"meals":[{"idMeal":"3","strMeal":"B"},{"idMeal":"1","strMeal":"A"}]}"""

        val result = repository(body, expectedUrl = "$SEARCH_URL?s=x").searchRecipes("x")

        assertEquals(listOf("3", "1"), assertIs<Result.Success<List<Recipe>>>(result).data.map { it.id })
    }

    @Test
    fun searchRecipesFailsWithNoConnectionOnNetworkError() = runTest {
        assertEquals(
            Result.Failure(DataError.NoConnection),
            repository("$SEARCH_URL?s=x") { throw IOException("offline") }.searchRecipes("x"),
        )
    }

    @Test
    fun getRecipeOpensASearchResultWithoutALookup() = runTest {
        val requestedUrls = mutableListOf<String>()
        val engine = MockEngine { request ->
            requestedUrls += request.url.toString()
            respond(MEAL_JSON, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = createTheMealDbHttpClient(engine)
        val repository = RecipeRepositoryImpl(client, database.favoriteRecipeDao(), RecipeCache())

        repository.searchRecipes("tart")
        val result = repository.getRecipe("52923")

        assertEquals("Canadian Butter Tarts", assertIs<Result.Success<Recipe>>(result).data.name)
        assertEquals(listOf("$SEARCH_URL?s=tart"), requestedUrls)
    }

    @Test
    fun getRecipePrefersASavedFavoriteOverASearchResult() = runTest {
        val saved = testRecipe("52923")
        database.favoriteRecipeDao().upsert(saved.toFavoriteRecipeEntity(savedAt = 1))
        val repository = repository(MEAL_JSON, expectedUrl = "$SEARCH_URL?s=tart")

        repository.searchRecipes("tart")

        assertEquals(Result.Success(saved), repository.getRecipe("52923"))
    }

    private companion object {
        const val SEARCH_URL = "https://www.themealdb.com/api/json/v2/1/search.php"
        const val RANDOM_URL = "https://www.themealdb.com/api/json/v2/1/random.php"
        const val LOOKUP_URL = "https://www.themealdb.com/api/json/v2/1/lookup.php"

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

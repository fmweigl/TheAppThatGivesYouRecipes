package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeCacheTest {

    private val cache = RecipeCache()

    private fun recipe(id: Int, name: String = "Recipe $id") = Recipe(id = id.toString(), name = name)

    @Test
    fun returnsNullForUnknownId() {
        assertNull(cache["1"])
    }

    @Test
    fun returnsStoredRecipeById() {
        cache.putAll(listOf(recipe(1), recipe(2)))

        assertEquals(recipe(1), cache["1"])
        assertEquals(recipe(2), cache["2"])
    }

    @Test
    fun storingAgainReplacesTheRecipe() {
        cache.putAll(listOf(recipe(1, "Old")))
        cache.putAll(listOf(recipe(1, "New")))

        assertEquals("New", cache["1"]?.name)
    }

    @Test
    fun keepsAtMostOneHundredRecipes() {
        cache.putAll((1..100).map(::recipe))
        assertEquals(recipe(1), cache["1"])

        cache.putAll(listOf(recipe(101)))

        assertNull(cache["1"], "the oldest recipe is dropped")
        assertEquals(recipe(2), cache["2"])
        assertEquals(recipe(101), cache["101"])
    }

    @Test
    fun aRecipeStoredAgainIsTheNewestAndSurvivesLonger() {
        cache.putAll((1..100).map(::recipe))
        cache.putAll(listOf(recipe(1)))

        cache.putAll(listOf(recipe(101)))

        assertEquals(recipe(1), cache["1"])
        assertNull(cache["2"], "the now-oldest recipe is dropped")
    }

    @Test
    fun aSingleBatchLargerThanTheLimitKeepsItsLastRecipes() {
        cache.putAll((1..150).map(::recipe))

        assertNull(cache["50"])
        assertEquals(recipe(51), cache["51"])
        assertEquals(recipe(150), cache["150"])
    }
}

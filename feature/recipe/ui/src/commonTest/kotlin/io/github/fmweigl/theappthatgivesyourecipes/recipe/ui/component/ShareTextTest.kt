package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import kotlin.test.Test
import kotlin.test.assertEquals

class ShareTextTest {

    private val recipe = Recipe(
        id = "1",
        name = "Shakshuka",
        category = "Vegetarian",
        area = "Tunisian",
        instructions = "Heat the oil.\r\nAdd the eggs.\r\n",
        sourceUrl = "https://example.com/shakshuka",
        ingredients = listOf(Ingredient("Olive Oil", "2 tbsp"), Ingredient("Eggs", "4")),
    )

    private fun labels(sourceLine: String? = "Source: https://example.com/shakshuka") = ShareTextLabels(
        ingredients = "Ingredients",
        instructions = "Instructions",
        sourceLine = sourceLine,
        sharedFrom = "Shared from The App That Gives You Recipes",
    )

    @Test
    fun fullRecipe() {
        assertEquals(
            """
            Shakshuka
            Vegetarian · Tunisian

            Ingredients
            • 2 tbsp Olive Oil
            • 4 Eggs

            Instructions
            Heat the oil.
            Add the eggs.

            Source: https://example.com/shakshuka
            Shared from The App That Gives You Recipes
            """.trimIndent(),
            recipe.toShareText(labels()),
        )
    }

    @Test
    fun withoutCategoryAndAreaTheNameStandsAlone() {
        val text = recipe.copy(category = null, area = null).toShareText(labels())

        assertEquals("Shakshuka\n\nIngredients", text.substringBefore("\n•"))
    }

    @Test
    fun onlyAreaIsShownWithoutSeparator() {
        val text = recipe.copy(category = null).toShareText(labels())

        assertEquals("Shakshuka\nTunisian", text.lines().take(2).joinToString("\n"))
    }

    @Test
    fun ingredientWithoutMeasureIsJustItsName() {
        val text = recipe.copy(ingredients = listOf(Ingredient("Salt", " "))).toShareText(labels())

        assertEquals(listOf("• Salt"), text.lines().filter { it.startsWith("•") })
    }

    @Test
    fun missingSectionsAreLeftOut() {
        val text = recipe.copy(instructions = " ", ingredients = emptyList()).toShareText(labels(sourceLine = null))

        assertEquals("Shakshuka\nVegetarian · Tunisian\n\nShared from The App That Gives You Recipes", text)
    }
}

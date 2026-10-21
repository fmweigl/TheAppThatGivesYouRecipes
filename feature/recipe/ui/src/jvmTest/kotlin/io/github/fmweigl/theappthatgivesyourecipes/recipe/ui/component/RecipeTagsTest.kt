package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import kotlin.test.Test
import kotlin.test.assertEquals

class RecipeTagsTest {
    @Test
    fun dropsRepeatsThatDifferOnlyInCaseAndKeepsOrderAndSpelling() {
        assertEquals(listOf("Spicy", "Breakfast"), listOf("Spicy", "Breakfast", "spicy", "BREAKFAST").distinctTags())
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Recipes from search results, by id, so a result opens without another request. In memory only,
 * gone with the process; keeps the [MAX_RECIPES] most recently stored.
 */
internal class RecipeCache {

    // A list, oldest first: a StateFlow ignores a map that only differs in order, so a stored-again
    // recipe would not move to the newest end.
    private val recipes = MutableStateFlow<List<Recipe>>(emptyList())

    operator fun get(id: String): Recipe? = recipes.value.lastOrNull { it.id == id }

    fun putAll(newRecipes: List<Recipe>) {
        recipes.update { cached ->
            val storedIds = newRecipes.mapTo(HashSet()) { it.id }
            // Recipes stored again drop out of their old place and join the newest end.
            (cached.filterNot { it.id in storedIds } + newRecipes.distinctBy { it.id }).takeLast(MAX_RECIPES)
        }
    }

    private companion object {
        const val MAX_RECIPES = 100
    }
}

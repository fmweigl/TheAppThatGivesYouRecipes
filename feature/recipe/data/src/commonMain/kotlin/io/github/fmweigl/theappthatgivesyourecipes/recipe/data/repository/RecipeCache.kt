package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Recipes from search results, by id, so a result opens without another request. In memory only,
 * gone with the process; keeps the [MAX_RECIPES] most recently stored.
 */
internal class RecipeCache {

    private val recipes = MutableStateFlow<Map<String, Recipe>>(emptyMap())

    operator fun get(id: String): Recipe? = recipes.value[id]

    fun putAll(newRecipes: List<Recipe>) {
        recipes.update { cached ->
            val merged = LinkedHashMap(cached)
            newRecipes.forEach { recipe ->
                // Removed first, so a stored-again recipe moves to the newest end.
                merged.remove(recipe.id)
                merged[recipe.id] = recipe
            }
            merged.entries.toList().takeLast(MAX_RECIPES).associate { it.key to it.value }
        }
    }

    private companion object {
        const val MAX_RECIPES = 100
    }
}

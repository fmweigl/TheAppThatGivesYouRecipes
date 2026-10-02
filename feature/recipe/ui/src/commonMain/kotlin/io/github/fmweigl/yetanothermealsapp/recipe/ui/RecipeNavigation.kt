package io.github.fmweigl.yetanothermealsapp.recipe.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe.RandomRecipeRoute
import io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail.RecipeRoute
import kotlinx.serialization.Serializable

/** Navigation key of the random recipe screen, the root of its tab. */
@Serializable
data object RandomRecipeNavKey : NavKey

/** Navigation key of the recipe with [recipeId], opened from a list such as the favorites. */
@Serializable
data class RecipeNavKey(val recipeId: String) : NavKey

/** Registers the recipe feature's screens; [onBack] leaves the recipe opened with [RecipeNavKey]. */
fun EntryProviderScope<NavKey>.recipeEntries(onBack: () -> Unit) {
    entry<RandomRecipeNavKey> { RandomRecipeRoute() }
    entry<RecipeNavKey> { key -> RecipeRoute(recipeId = key.recipeId, onBack = onBack) }
}

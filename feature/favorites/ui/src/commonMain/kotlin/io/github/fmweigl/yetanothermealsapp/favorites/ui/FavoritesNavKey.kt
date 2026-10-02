package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the favorites list, the root of its tab. */
@Serializable
data object FavoritesNavKey : NavKey

/**
 * Registers the favorites list. [onOpenRecipe] opens a favorite; the app maps it to the recipe
 * feature's screen, which this feature doesn't know.
 */
fun EntryProviderScope<NavKey>.favoritesEntry(onOpenRecipe: (recipeId: String) -> Unit) {
    entry<FavoritesNavKey> { FavoritesRoute(onOpenRecipe = onOpenRecipe) }
}

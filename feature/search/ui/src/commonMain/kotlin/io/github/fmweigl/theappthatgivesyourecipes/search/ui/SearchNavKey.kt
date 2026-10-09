package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/** Navigation key of the search screen, the root of its tab. */
@Serializable
data object SearchNavKey : NavKey

/**
 * Registers the search screen. [onOpenRecipe] opens a result; the app maps it to the recipe
 * feature's screen, which this feature doesn't know. [tabReselected] emits when the user taps the
 * Search tab while it is already shown: the screen then scrolls to the top and focuses the field.
 */
fun EntryProviderScope<NavKey>.searchEntry(
    onOpenRecipe: (recipeId: String) -> Unit,
    tabReselected: Flow<Unit>,
) {
    entry<SearchNavKey> { SearchRoute(onOpenRecipe = onOpenRecipe, tabReselected = tabReselected) }
}

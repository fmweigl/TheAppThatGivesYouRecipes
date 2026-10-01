package io.github.fmweigl.yetanothermealsapp.randomrecipe.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the random recipe screen. */
@Serializable
data object RandomRecipeNavKey : NavKey

/** Registers the random recipe screen for [RandomRecipeNavKey]. */
fun EntryProviderScope<NavKey>.randomRecipeEntry() {
    entry<RandomRecipeNavKey> { RandomRecipeRoute() }
}

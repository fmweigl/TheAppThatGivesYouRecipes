package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the about screen. */
@Serializable
data object AboutNavKey : NavKey

/** Registers the about screen for [AboutNavKey]. */
fun EntryProviderScope<NavKey>.aboutEntry() {
    entry<AboutNavKey> { AboutScreen() }
}

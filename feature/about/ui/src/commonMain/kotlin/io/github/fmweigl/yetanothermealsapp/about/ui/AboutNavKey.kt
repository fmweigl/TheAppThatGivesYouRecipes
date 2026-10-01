package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the about screen. */
@Serializable
data object AboutNavKey : NavKey

/**
 * Registers the about screen for [AboutNavKey]. [loadLibrariesJson] returns the AboutLibraries
 * JSON of the app's dependencies; only the app module can generate it, as it sees all of them.
 */
fun EntryProviderScope<NavKey>.aboutEntry(loadLibrariesJson: suspend () -> String) {
    entry<AboutNavKey> { AboutRoute(loadLibrariesJson) }
}

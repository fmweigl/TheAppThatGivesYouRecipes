package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the about screen, the root of the about tab. */
@Serializable
data object AboutNavKey : NavKey

/** Navigation key of the list of libraries the app uses, opened from the about screen. */
@Serializable
data object LibrariesNavKey : NavKey

/**
 * Registers the about screen for [AboutNavKey] and the library list for [LibrariesNavKey].
 * [onLibrariesClick] should navigate to [LibrariesNavKey], [onBack] leave it.
 * [loadLibrariesJson] returns the AboutLibraries JSON of the app's dependencies; only the app
 * module can generate it, as it sees all of them.
 */
fun EntryProviderScope<NavKey>.aboutEntries(
    onLibrariesClick: () -> Unit,
    onBack: () -> Unit,
    loadLibrariesJson: suspend () -> String,
) {
    entry<AboutNavKey> {
        // The license screen doesn't exist yet.
        AboutScreen(onLicenseClick = {}, onLibrariesClick = onLibrariesClick)
    }
    entry<LibrariesNavKey> { LibrariesRoute(loadLibrariesJson = loadLibrariesJson, onBack = onBack) }
}

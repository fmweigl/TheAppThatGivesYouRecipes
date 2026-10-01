package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the about screen, the root of the about tab. */
@Serializable
data object AboutNavKey : NavKey

/** Navigation key of the app's license, opened from the about screen. */
@Serializable
data object LicenseNavKey : NavKey

/** Navigation key of the list of libraries the app uses, opened from the about screen. */
@Serializable
data object LibrariesNavKey : NavKey

/**
 * Registers the about screen for [AboutNavKey], the license for [LicenseNavKey] and the library
 * list for [LibrariesNavKey]. [onLicenseClick] and [onLibrariesClick] should navigate to those keys,
 * [onBack] leave them. Only the app module can provide the content: [loadLicenseText] returns the
 * app's license text, [loadLibrariesJson] the AboutLibraries JSON of the app's dependencies.
 */
fun EntryProviderScope<NavKey>.aboutEntries(
    onLicenseClick: () -> Unit,
    onLibrariesClick: () -> Unit,
    onBack: () -> Unit,
    loadLicenseText: suspend () -> String,
    loadLibrariesJson: suspend () -> String,
) {
    entry<AboutNavKey> { AboutScreen(onLicenseClick = onLicenseClick, onLibrariesClick = onLibrariesClick) }
    entry<LicenseNavKey> { LicenseRoute(loadLicenseText = loadLicenseText, onBack = onBack) }
    entry<LibrariesNavKey> { LibrariesRoute(loadLibrariesJson = loadLibrariesJson, onBack = onBack) }
}

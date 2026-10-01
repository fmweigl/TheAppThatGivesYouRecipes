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

/** Navigation key of the app's privacy policy, opened from the about screen. */
@Serializable
data object PrivacyNavKey : NavKey

/** Navigation key of the list of libraries the app uses, opened from the about screen. */
@Serializable
data object LibrariesNavKey : NavKey

/**
 * Registers the about screen for [AboutNavKey] and the screens it opens: the license for
 * [LicenseNavKey], the privacy policy for [PrivacyNavKey] and the library list for [LibrariesNavKey].
 * [onLicenseClick], [onPrivacyClick] and [onLibrariesClick] should navigate to those keys, [onBack]
 * leave them. [content] provides what the screens show.
 */
fun EntryProviderScope<NavKey>.aboutEntries(
    onLicenseClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onLibrariesClick: () -> Unit,
    onBack: () -> Unit,
    content: AboutContent,
) {
    entry<AboutNavKey> {
        AboutScreen(
            onLicenseClick = onLicenseClick,
            onPrivacyClick = onPrivacyClick,
            onLibrariesClick = onLibrariesClick,
        )
    }
    entry<LicenseNavKey> {
        TextDocumentRoute(title = "License", loadText = content.loadLicenseText, onBack = onBack)
    }
    entry<PrivacyNavKey> {
        TextDocumentRoute(title = "Privacy", loadText = content.loadPrivacyText, onBack = onBack)
    }
    entry<LibrariesNavKey> { LibrariesRoute(loadLibrariesJson = content.loadLibrariesJson, onBack = onBack) }
}

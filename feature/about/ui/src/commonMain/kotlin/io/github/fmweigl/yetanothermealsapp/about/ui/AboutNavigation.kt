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

/** Navigation key of the app's attributions (data sources), opened from the about screen. */
@Serializable
data object AttributionsNavKey : NavKey

/**
 * Registers the about screen for [AboutNavKey] and the screens it opens: the license for
 * [LicenseNavKey], the library list for [LibrariesNavKey], the privacy policy for [PrivacyNavKey]
 * and the attributions for [AttributionsNavKey]. The about screen calls [onNavigate] with one of
 * these keys to open it; [onBack] leaves it. [content] provides what the screens show.
 */
fun EntryProviderScope<NavKey>.aboutEntries(
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    content: AboutContent,
) {
    entry<AboutNavKey> {
        AboutScreen(
            onLicenseClick = { onNavigate(LicenseNavKey) },
            onLibrariesClick = { onNavigate(LibrariesNavKey) },
            onPrivacyClick = { onNavigate(PrivacyNavKey) },
            onAttributionsClick = { onNavigate(AttributionsNavKey) },
        )
    }
    entry<LicenseNavKey> {
        TextDocumentRoute(title = "License", loadText = content.loadLicenseText, onBack = onBack)
    }
    entry<LibrariesNavKey> { LibrariesRoute(loadLibrariesJson = content.loadLibrariesJson, onBack = onBack) }
    entry<PrivacyNavKey> {
        TextDocumentRoute(title = "Privacy", loadText = content.loadPrivacyText, onBack = onBack)
    }
    entry<AttributionsNavKey> {
        TextDocumentRoute(title = "Attributions", loadText = content.loadAttributionsText, onBack = onBack)
    }
}

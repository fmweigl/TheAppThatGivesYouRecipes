package io.github.fmweigl.theappthatgivesyourecipes.about.ui

/**
 * The content of the about screens, which only the app module can provide: [loadLicenseText] returns
 * the app's license, [loadPrivacyText] its privacy policy, [loadAttributionsText] its attributions
 * (both Markdown), [loadLibrariesJson] the AboutLibraries JSON of the app's dependencies. [versionName]
 * and [versionCode] are the app's version, as the store or installer shows it.
 */
class AboutContent(
    val versionName: String,
    val versionCode: Int,
    val loadLicenseText: suspend () -> String,
    val loadPrivacyText: suspend () -> String,
    val loadAttributionsText: suspend () -> String,
    val loadLibrariesJson: suspend () -> String,
)

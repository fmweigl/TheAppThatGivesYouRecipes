package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier

/** Loads the license text and passes its paragraphs to [LicenseScreen]. */
@Composable
internal fun LicenseRoute(
    loadLicenseText: suspend () -> String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val paragraphs by produceState<List<String>?>(initialValue = null, loadLicenseText) {
        value = toParagraphs(loadLicenseText())
    }
    LicenseScreen(paragraphs = paragraphs, onBack = onBack, modifier = modifier)
}

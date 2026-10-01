package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier

/** Loads a text document (license, privacy policy) and passes its blocks to [TextDocumentScreen]. */
@Composable
internal fun TextDocumentRoute(
    title: String,
    loadText: suspend () -> String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val blocks by produceState<List<TextBlock>?>(initialValue = null, loadText) {
        value = toTextBlocks(loadText())
    }
    TextDocumentScreen(title = title, blocks = blocks, onBack = onBack, modifier = modifier)
}

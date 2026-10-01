package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Loads a text document (license, privacy policy) and passes its blocks to [TextDocumentScreen]. */
@Composable
internal fun TextDocumentRoute(
    title: StringResource,
    loadText: suspend () -> String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val blocks by produceState<List<TextBlock>?>(initialValue = null, loadText) {
        value = toTextBlocks(loadText())
    }
    TextDocumentScreen(title = stringResource(title), blocks = blocks, onBack = onBack, modifier = modifier)
}

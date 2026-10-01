package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.fmweigl.yetanothermealsapp.about.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.about.ui.resources.loading
import org.jetbrains.compose.resources.stringResource

/** A text document such as the license or the privacy policy; [blocks] is null while loading. */
@Composable
internal fun TextDocumentScreen(
    title: String,
    blocks: List<TextBlock>?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        BackTopAppBar(title = title, onBack = onBack)
        if (blocks == null) {
            Box(Modifier.weight(1f).fillMaxSize()) {
                val loading = stringResource(Res.string.loading)
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center).semantics { contentDescription = loading },
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(blocks) { block ->
                    Text(
                        block.text,
                        style = styleFor(block.headingLevel),
                        modifier = if (block.headingLevel > 0) Modifier.semantics { heading() } else Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun styleFor(headingLevel: Int): TextStyle = with(MaterialTheme.typography) {
    when (headingLevel) {
        0 -> bodyMedium
        1 -> titleLarge
        else -> titleMedium
    }
}

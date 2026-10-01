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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

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
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(blocks) { Text(it.text, style = styleFor(it.headingLevel)) }
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

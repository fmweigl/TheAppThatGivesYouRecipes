package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.try_again
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.toMessage
import org.jetbrains.compose.resources.stringResource

/** The message for [error] with a "Try again" button; [onRetry] is null for errors retrying can't fix. */
@Composable
internal fun ErrorMessage(
    error: DataError,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val message = stringResource(error.toMessage())
    Column(
        // Announced by screen readers when it appears (the focus stays on the button that failed).
        modifier = modifier.fillMaxSize().padding(16.dp).semantics { paneTitle = message },
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            Button(onClick = onRetry) { Text(stringResource(Res.string.try_again)) }
        }
    }
}

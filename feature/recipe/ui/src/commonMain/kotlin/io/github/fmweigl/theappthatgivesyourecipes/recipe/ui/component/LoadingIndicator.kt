package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.loading
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoadingIndicator(modifier: Modifier = Modifier) {
    val loading = stringResource(Res.string.loading)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.semantics { contentDescription = loading })
    }
}

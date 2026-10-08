package io.github.fmweigl.theappthatgivesyourecipes.about.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

/** Loads the library list and passes it to [LibrariesScreen]. */
@Composable
internal fun LibrariesRoute(
    loadLibrariesJson: suspend () -> String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val libraries by produceLibraries(loadLibrariesJson)
    LibrariesScreen(libraries = libraries, onBack = onBack, modifier = modifier)
}

package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

/** Loads the library list and passes it to [AboutScreen]. */
@Composable
internal fun AboutRoute(
    loadLibrariesJson: suspend () -> String,
    modifier: Modifier = Modifier,
) {
    val libraries by produceLibraries(loadLibrariesJson)
    AboutScreen(libraries = libraries, modifier = modifier)
}

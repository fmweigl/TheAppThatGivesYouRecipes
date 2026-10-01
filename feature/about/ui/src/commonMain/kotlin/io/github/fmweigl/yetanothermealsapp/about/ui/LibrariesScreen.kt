package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

/** The libraries the app uses with their licenses; [libraries] is null while loading. */
@Composable
internal fun LibrariesScreen(
    libraries: Libs?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        BackTopAppBar(title = "Libraries", onBack = onBack)
        LibrariesContainer(libraries = libraries, modifier = Modifier.weight(1f))
    }
}

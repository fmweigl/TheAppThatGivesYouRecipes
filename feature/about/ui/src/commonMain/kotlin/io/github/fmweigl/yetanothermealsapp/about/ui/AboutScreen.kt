package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

/** The libraries the app uses with their licenses; [libraries] is null while loading. */
@Composable
internal fun AboutScreen(libraries: Libs?, modifier: Modifier = Modifier) {
    LibrariesContainer(libraries = libraries, modifier = modifier.fillMaxSize())
}

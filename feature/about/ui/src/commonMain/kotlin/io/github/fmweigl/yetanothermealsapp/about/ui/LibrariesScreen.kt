package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

/** The libraries the app uses with their licenses; [libraries] is null while loading. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibrariesScreen(
    libraries: Libs?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        // A back button, because desktop has no system back.
        TopAppBar(
            title = { Text("Libraries") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
        LibrariesContainer(libraries = libraries, modifier = Modifier.weight(1f))
    }
}

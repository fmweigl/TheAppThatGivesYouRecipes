package io.github.fmweigl.yetanothermealsapp.about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AboutScreen(
    onLicenseClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onLibrariesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onLicenseClick) { Text("License") }
        Button(onClick = onLibrariesClick) { Text("Libraries") }
        Button(onClick = onPrivacyClick) { Text("Privacy") }
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.attributions
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.libraries
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.license
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.privacy
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutScreen(
    onLicenseClick: () -> Unit,
    onLibrariesClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAttributionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onLicenseClick) { Text(stringResource(Res.string.license)) }
        Button(onClick = onLibrariesClick) { Text(stringResource(Res.string.libraries)) }
        Button(onClick = onPrivacyClick) { Text(stringResource(Res.string.privacy)) }
        Button(onClick = onAttributionsClick) { Text(stringResource(Res.string.attributions)) }
    }
}

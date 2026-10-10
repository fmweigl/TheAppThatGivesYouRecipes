package io.github.fmweigl.theappthatgivesyourecipes.about.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.attributions
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.libraries
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.license
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.privacy
import io.github.fmweigl.theappthatgivesyourecipes.about.ui.resources.version
import org.jetbrains.compose.resources.stringResource

/** The about tab's root: a button for each document and the app's version below. */
@Composable
internal fun AboutScreen(
    onNavigate: (NavKey) -> Unit,
    versionName: String,
    versionCode: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        // Scrolls when the window is short (phones in landscape, large fonts); centered when it fits.
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { onNavigate(LicenseNavKey) }) { Text(stringResource(Res.string.license)) }
        Button(onClick = { onNavigate(LibrariesNavKey) }) { Text(stringResource(Res.string.libraries)) }
        Button(onClick = { onNavigate(PrivacyNavKey) }) { Text(stringResource(Res.string.privacy)) }
        Button(onClick = { onNavigate(AttributionsNavKey) }) { Text(stringResource(Res.string.attributions)) }
        Text(
            text = stringResource(Res.string.version, versionName, versionCode),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

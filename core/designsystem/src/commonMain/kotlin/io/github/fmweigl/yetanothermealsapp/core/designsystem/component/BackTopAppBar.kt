package io.github.fmweigl.yetanothermealsapp.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import io.github.fmweigl.yetanothermealsapp.core.designsystem.resources.Res
import io.github.fmweigl.yetanothermealsapp.core.designsystem.resources.back
import org.jetbrains.compose.resources.stringResource

/** Top bar of screens below a tab's root, with a back button: desktop has no system back. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopAppBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, modifier = Modifier.semantics { heading() }) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
            }
        },
    )
}

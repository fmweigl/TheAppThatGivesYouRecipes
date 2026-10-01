package io.github.fmweigl.yetanothermealsapp.helloworld

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation key of the placeholder screen in the second tab. */
@Serializable
internal data object HelloWorldNavKey : NavKey

internal fun EntryProviderScope<NavKey>.helloWorldEntry() {
    entry<HelloWorldNavKey> { HelloWorldScreen() }
}

@Composable
private fun HelloWorldScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Hello World")
    }
}

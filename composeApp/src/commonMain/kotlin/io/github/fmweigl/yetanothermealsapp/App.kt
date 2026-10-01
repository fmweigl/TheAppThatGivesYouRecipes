package io.github.fmweigl.yetanothermealsapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.fmweigl.yetanothermealsapp.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}

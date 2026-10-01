package io.github.fmweigl.yetanothermealsapp

import androidx.compose.runtime.Composable
import io.github.fmweigl.yetanothermealsapp.core.designsystem.MealsTheme
import io.github.fmweigl.yetanothermealsapp.navigation.AppNavigation

@Composable
fun App() {
    MealsTheme {
        AppNavigation()
    }
}

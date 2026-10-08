package io.github.fmweigl.theappthatgivesyourecipes

import androidx.compose.runtime.Composable
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.MealsTheme
import io.github.fmweigl.theappthatgivesyourecipes.navigation.AppNavigation

@Composable
fun App() {
    MealsTheme {
        AppNavigation()
    }
}

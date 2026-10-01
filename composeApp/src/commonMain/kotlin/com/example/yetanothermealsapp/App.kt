package com.example.yetanothermealsapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe
import com.example.yetanothermealsapp.randomrecipe.ui.RandomRecipeScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            // Placeholder until data is wired in
            RandomRecipeScreen(Recipe(id = "0", name = "Random recipe"))
        }
    }
}

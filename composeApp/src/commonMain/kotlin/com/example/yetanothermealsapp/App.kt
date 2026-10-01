package com.example.yetanothermealsapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.yetanothermealsapp.randomrecipe.ui.RandomRecipeEntry

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RandomRecipeEntry(modifier = Modifier.safeContentPadding())
        }
    }
}

package com.example.yetanothermealsapp.randomrecipe.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe

@Composable
fun RandomRecipeScreen(recipe: Recipe) {
    Text(recipe.name)
}

package com.example.yetanothermealsapp.randomrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe

@Composable
internal fun RandomRecipeScreen(
    uiState: RandomRecipeUiState,
    onLoadAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        RandomRecipeUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is RandomRecipeUiState.Error -> Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                uiState.error.toMessage(),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onLoadAnother) { Text("Try again") }
        }
        is RandomRecipeUiState.Success -> RecipeDetails(uiState.recipe, onLoadAnother, modifier)
    }
}

@Composable
private fun RecipeDetails(
    recipe: Recipe,
    onLoadAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
            )
        }
        item {
            Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
            val subtitle = listOfNotNull(recipe.category, recipe.area).joinToString(" · ")
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Button(onClick = onLoadAnother, modifier = Modifier.fillMaxWidth()) { Text("Show another recipe") }
        }
        if (recipe.ingredients.isNotEmpty()) {
            item { SectionTitle("Ingredients") }
            items(recipe.ingredients) { ingredient ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(ingredient.name, modifier = Modifier.weight(1f))
                    Text(ingredient.measure, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        recipe.instructions?.let { instructions ->
            item { SectionTitle("Instructions") }
            item { Text(instructions) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

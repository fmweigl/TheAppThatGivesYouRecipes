package io.github.fmweigl.yetanothermealsapp.randomrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Recipe
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.RandomRecipeUiState.Content

@Composable
internal fun RandomRecipeScreen(
    uiState: RandomRecipeUiState,
    onShowNext: () -> Unit,
    onShowPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (val content = uiState.content) {
                Content.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is Content.Error -> ErrorMessage(content, onRetry = onShowNext)
                // A new list state per recipe, so each one starts scrolled to the top.
                is Content.Success -> key(content.recipe.id) { RecipeDetails(content.recipe) }
            }
        }
        HorizontalDivider()
        RecipeNavigationBar(
            canShowPrevious = uiState.canShowPrevious,
            canShowNext = uiState.content !is Content.Loading,
            onShowPrevious = onShowPrevious,
            onShowNext = onShowNext,
        )
    }
}

@Composable
private fun ErrorMessage(error: Content.Error, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            error.error.toMessage(),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) { Text("Try again") }
    }
}

@Composable
private fun RecipeNavigationBar(
    canShowPrevious: Boolean,
    canShowNext: Boolean,
    onShowPrevious: () -> Unit,
    onShowNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onShowPrevious,
            enabled = canShowPrevious,
            modifier = Modifier.weight(1f),
        ) { Text("‹ Previous") }
        Button(
            onClick = onShowNext,
            enabled = canShowNext,
            modifier = Modifier.weight(1f),
        ) { Text("Next ›") }
    }
}

@Composable
private fun RecipeDetails(
    recipe: Recipe,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState(),
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
                    .clip(MaterialTheme.shapes.large),
            )
        }
        item {
            Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
            val subtitle = listOfNotNull(recipe.category, recipe.area).joinToString(" · ")
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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

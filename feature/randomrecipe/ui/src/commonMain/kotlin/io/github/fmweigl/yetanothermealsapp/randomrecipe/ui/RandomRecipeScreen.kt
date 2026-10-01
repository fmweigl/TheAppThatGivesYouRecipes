package io.github.fmweigl.yetanothermealsapp.randomrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.Recipe
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.RandomRecipeUiState.Content
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.ingredients
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.instructions
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.loading
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.next_recipe
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.previous_recipe
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.resources.try_again
import org.jetbrains.compose.resources.stringResource

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
                Content.Loading -> {
                    val loading = stringResource(Res.string.loading)
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center).semantics { contentDescription = loading },
                    )
                }
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
    val message = stringResource(error.error.toMessage())
    Column(
        // Announced by screen readers when it appears (the focus stays on the button that failed).
        modifier = Modifier.fillMaxSize().padding(16.dp).semantics { paneTitle = message },
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) { Text(stringResource(Res.string.try_again)) }
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
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.previous_recipe))
        }
        Button(
            onClick = onShowNext,
            enabled = canShowNext,
            modifier = Modifier.weight(1f),
        ) {
            Text(stringResource(Res.string.next_recipe))
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
        }
    }
}

@Composable
private fun RecipeDetails(
    recipe: Recipe,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        // Announced by screen readers when a recipe appears (the focus stays on "Next").
        modifier = modifier.fillMaxSize().semantics { paneTitle = recipe.name },
        state = rememberLazyListState(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AsyncImage(
                model = recipe.imageUrl,
                // Decorative: the recipe's name follows right below.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large),
            )
        }
        item {
            Text(
                recipe.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
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
            item { SectionTitle(stringResource(Res.string.ingredients)) }
            items(recipe.ingredients) { ingredient ->
                // One element for screen readers: "Sushi Rice, 300ml".
                Row(
                    modifier = Modifier.semantics(mergeDescendants = true) {},
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(ingredient.name, modifier = Modifier.weight(1f))
                    Text(ingredient.measure, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        recipe.instructions?.let { instructions ->
            item { SectionTitle(stringResource(Res.string.instructions)) }
            item { Text(instructions) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.semantics { heading() },
    )
}

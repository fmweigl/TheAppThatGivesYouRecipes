package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.ingredients
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.instructions
import org.jetbrains.compose.resources.stringResource

/** The whole recipe: image, name with the [FavoriteButton], ingredients and instructions. */
@Composable
internal fun RecipeDetails(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        // Announced by screen readers when the recipe appears (the focus stays where it was, e.g. on "Next").
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
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
                FavoriteButton(isFavorite = isFavorite, onToggle = onToggleFavorite)
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

package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.ingredients
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.instructions
import org.jetbrains.compose.resources.stringResource

/**
 * The widest a column of recipe text gets: the single column on medium windows, the ingredients and
 * instructions pane, and the Previous/Next row. Longer lines are hard to follow.
 */
internal val MaxColumnWidth = 640.dp

/** The image on medium windows and in the two-pane layout: wider than tall, so the name stays in view. */
private const val WIDE_IMAGE_ASPECT_RATIO = 4f / 3f

/** Share of the window's width the image pane gets in the two-pane layout. */
private const val IMAGE_PANE_WEIGHT = 0.4f

/**
 * The whole recipe: image, name with the [FavoriteButton], ingredients and instructions. Its layout
 * follows the window's size class: one column on compact widths (phones in portrait), the same
 * column centered and with a wider image on medium widths (tablets in portrait), and two panes,
 * name and image next to ingredients and instructions, each scrolling on its own, on expanded
 * widths (tablets in landscape) and on medium widths with a compact height (phones in landscape),
 * where the column's image would be taller than the window.
 */
@Composable
internal fun RecipeDetails(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    // Announced by screen readers when the recipe appears (the focus stays where it was, e.g. on "Next").
    val paneModifier = modifier.fillMaxSize().semantics { paneTitle = recipe.name }
    val isMediumWidth = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isCompactHeight = !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    when {
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
            isMediumWidth && isCompactHeight ->
            TwoPaneRecipe(recipe, isFavorite, onToggleFavorite, paneModifier)
        isMediumWidth ->
            SingleColumnRecipe(
                recipe,
                isFavorite,
                onToggleFavorite,
                imageAspectRatio = WIDE_IMAGE_ASPECT_RATIO,
                modifier = paneModifier,
                itemModifier = Modifier.widthIn(max = MaxColumnWidth),
            )
        else -> SingleColumnRecipe(recipe, isFavorite, onToggleFavorite, imageAspectRatio = 1f, paneModifier)
    }
}

/** One scrolling column. [itemModifier] goes on every item, before it fills the width. */
@Composable
private fun SingleColumnRecipe(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    imageAspectRatio: Float,
    modifier: Modifier,
    itemModifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val fullWidth = itemModifier.fillMaxWidth()
        item { RecipeImage(recipe, imageAspectRatio, fullWidth) }
        item { RecipeTitle(recipe, isFavorite, onToggleFavorite, fullWidth) }
        recipeBody(recipe, fullWidth)
    }
}

/**
 * Name and image on the start side, ingredients and instructions on the end side. The name (with the
 * heart) comes first: in a short window, such as a phone in landscape, the image would push it out
 * of view. Each pane is its own traversal group, so screen readers finish one before the other.
 */
@Composable
private fun TwoPaneRecipe(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier,
) {
    Row(modifier) {
        Column(
            modifier = Modifier
                .weight(IMAGE_PANE_WEIGHT)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RecipeTitle(recipe, isFavorite, onToggleFavorite)
            RecipeImage(recipe, WIDE_IMAGE_ASPECT_RATIO, Modifier.fillMaxWidth())
        }
        val bodyState = rememberLazyListState()
        LazyColumn(
            // Nothing in this pane takes focus, so on the desktop the pane itself does, to scroll by keyboard.
            modifier = Modifier.weight(1f - IMAGE_PANE_WEIGHT).fillMaxHeight().keyboardScrollable(bodyState),
            state = bodyState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            recipeBody(recipe, Modifier.widthIn(max = MaxColumnWidth).fillMaxWidth())
        }
    }
}

@Composable
private fun RecipeImage(recipe: Recipe, aspectRatio: Float, modifier: Modifier) {
    AsyncImage(
        model = recipe.imageUrl,
        // Decorative: the recipe's name is right next to it.
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(MaterialTheme.shapes.large),
    )
}

@Composable
private fun RecipeTitle(
    recipe: Recipe,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
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

/** Ingredients and instructions, each item with [itemModifier]. */
private fun LazyListScope.recipeBody(recipe: Recipe, itemModifier: Modifier) {
    if (recipe.ingredients.isNotEmpty()) {
        item { SectionTitle(stringResource(Res.string.ingredients), itemModifier) }
        items(recipe.ingredients) { ingredient ->
            // One element for screen readers: "Sushi Rice, 300ml".
            Row(
                modifier = itemModifier.semantics(mergeDescendants = true) {},
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(ingredient.name, modifier = Modifier.weight(1f))
                Text(ingredient.measure, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    recipe.instructions?.let { instructions ->
        item { SectionTitle(stringResource(Res.string.instructions), itemModifier) }
        item { Text(instructions, itemModifier) }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.semantics { heading() },
    )
}

package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.FavoriteButtonPlaceholder
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.Skeleton
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.SkeletonBlock
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.resources.loading
import org.jetbrains.compose.resources.stringResource

/** Widths of the placeholder name, "Category · Area" line and section headings, as fractions of their space. */
private const val NAME_FRACTION = 0.7f
private const val SUBTITLE_FRACTION = 0.4f
private const val SECTION_TITLE_FRACTION = 0.35f

/** Width of the placeholder measures, about that of "1 tbsp". */
private val MeasureWidth = 56.dp

/** Widths of the placeholder ingredients' names, as fractions of the row, so they don't look like a table. */
private val IngredientNameFractions = listOf(0.55f, 0.4f, 0.7f, 0.35f, 0.6f, 0.45f)

/** Widths of the placeholder instruction lines; the last one ends early, like a paragraph. */
private val InstructionLineFractions = listOf(1f, 0.95f, 1f, 0.9f, 0.6f)

/**
 * Stands in for [RecipeDetails] while the recipe loads: the design system's [Skeleton], with blocks
 * where the image, name, heart, ingredients and instructions will be, in the same layout
 * ([currentRecipeLayout]) and measurements, so nothing jumps when the recipe arrives. For screen
 * readers one element, "Loading", reported as a progress bar as the spinner before it was; it
 * doesn't scroll, the window cuts off what doesn't fit.
 */
@Composable
internal fun RecipeSkeleton(modifier: Modifier = Modifier) {
    val loading = stringResource(Res.string.loading)
    Skeleton(
        modifier.fillMaxSize().clearAndSetSemantics {
            contentDescription = loading
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
        },
    ) {
        when (currentRecipeLayout()) {
            RecipeLayout.TwoPanes -> TwoPaneSkeleton()
            RecipeLayout.CenteredColumn ->
                SingleColumnSkeleton(
                    imageAspectRatio = WIDE_IMAGE_ASPECT_RATIO,
                    itemModifier = Modifier.widthIn(max = MaxColumnWidth),
                )
            RecipeLayout.Column -> SingleColumnSkeleton(imageAspectRatio = 1f)
        }
    }
}

/** Mirrors [RecipeDetails]' single column: same padding, spacing and item width. */
@Composable
private fun SingleColumnSkeleton(imageAspectRatio: Float, itemModifier: Modifier = Modifier) {
    SkeletonColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val fullWidth = itemModifier.fillMaxWidth()
        ImagePlaceholder(imageAspectRatio, fullWidth)
        TitlePlaceholder(fullWidth)
        BodyPlaceholder(fullWidth)
    }
}

/** Mirrors [RecipeDetails]' two panes: name and image next to ingredients and instructions. */
@Composable
private fun TwoPaneSkeleton() {
    Row(Modifier.fillMaxSize()) {
        SkeletonColumn(Modifier.weight(IMAGE_PANE_WEIGHT).fillMaxHeight()) {
            TitlePlaceholder(Modifier.fillMaxWidth())
            ImagePlaceholder(WIDE_IMAGE_ASPECT_RATIO, Modifier.fillMaxWidth())
        }
        SkeletonColumn(Modifier.weight(1f - IMAGE_PANE_WEIGHT).fillMaxHeight()) {
            BodyPlaceholder(Modifier.widthIn(max = MaxColumnWidth).fillMaxWidth())
        }
    }
}

/** A column laid out as tall as its content needs and cut off at the bottom, like a list scrolled to the top. */
@Composable
private fun SkeletonColumn(
    modifier: Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clipToBounds()
            .wrapContentHeight(Alignment.Top, unbounded = true)
            .padding(ContentPadding),
        verticalArrangement = Arrangement.spacedBy(ItemSpacing),
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

@Composable
private fun ImagePlaceholder(aspectRatio: Float, modifier: Modifier) {
    SkeletonBlock(modifier.aspectRatio(aspectRatio), MaterialTheme.shapes.large)
}

/** The name, the "Category · Area" line and the heart. */
@Composable
private fun TitlePlaceholder(modifier: Modifier) {
    Row(modifier, verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            TextPlaceholder(MaterialTheme.typography.headlineMedium, Modifier.fillMaxWidth(NAME_FRACTION))
            TextPlaceholder(MaterialTheme.typography.titleSmall, Modifier.fillMaxWidth(SUBTITLE_FRACTION))
        }
        FavoriteButtonPlaceholder()
    }
}

/** The "Ingredients" heading with a few rows, then "Instructions" with a paragraph. */
@Composable
private fun BodyPlaceholder(modifier: Modifier) {
    val sectionTitle = MaterialTheme.typography.titleMedium
    val body = MaterialTheme.typography.bodyLarge
    SectionTitlePlaceholder(sectionTitle, modifier)
    IngredientNameFractions.forEach { fraction ->
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(IngredientSpacing)) {
            Box(Modifier.weight(1f)) { TextPlaceholder(body, Modifier.fillMaxWidth(fraction)) }
            TextPlaceholder(body, Modifier.width(MeasureWidth))
        }
    }
    SectionTitlePlaceholder(sectionTitle, modifier)
    Column(modifier) {
        InstructionLineFractions.forEach { fraction -> TextPlaceholder(body, Modifier.fillMaxWidth(fraction)) }
    }
}

/** A short heading line; [modifier] may already fill the width, so the bar gets its own box. */
@Composable
private fun SectionTitlePlaceholder(style: TextStyle, modifier: Modifier) {
    Box(modifier) { TextPlaceholder(style, Modifier.fillMaxWidth(SECTION_TITLE_FRACTION)) }
}

/**
 * One line of text in [style]: as tall as the line, with a bar the height of the font in it, so the
 * skeleton grows with the font size like the recipe does. The app's text styles all set both sizes.
 */
@Composable
private fun TextPlaceholder(style: TextStyle, modifier: Modifier) {
    val density = LocalDensity.current
    val lineHeight = with(density) { style.lineHeight.toDp() }
    val barHeight = with(density) { style.fontSize.toDp() }
    Box(modifier.height(lineHeight), contentAlignment = Alignment.CenterStart) {
        SkeletonBlock(Modifier.fillMaxWidth().height(barHeight))
    }
}

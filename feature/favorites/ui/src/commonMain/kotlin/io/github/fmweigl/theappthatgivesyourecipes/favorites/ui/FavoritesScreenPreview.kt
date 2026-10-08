package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.preview.DevicePreviews
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.preview.MealsPreview
import io.github.fmweigl.theappthatgivesyourecipes.favorites.ui.FavoritesUiState.Content

/** Favorites written for previews (not from TheMealDB). */
private val previewTeasers = listOf(
    RecipeTeaser("1", "Shakshuka", "Vegetarian · Tunisian", imageUrl = null),
    RecipeTeaser("2", "Spaghetti al pomodoro", "Pasta · Italian", imageUrl = null),
    RecipeTeaser("3", "Chicken katsu curry", "Chicken · Japanese", imageUrl = null),
    RecipeTeaser("4", "Lentil soup", "Vegetarian · Turkish", imageUrl = null),
    RecipeTeaser("5", "Fish tacos", "Seafood · Mexican", imageUrl = null),
)

@DevicePreviews
@Composable
private fun EmptyFavoritesScreenPreview() {
    MealsPreview {
        FavoritesScreen(
            uiState = FavoritesUiState(Content.Empty),
            onOpenRecipe = {},
            onRemove = {},
            onRemovalMessageClosed = {},
            onRestoreFailureMessageClosed = {},
            onRemoveFailureMessageClosed = {},
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@DevicePreviews
@Composable
private fun FavoritesScreenPreview() {
    MealsPreview {
        // Previews can't load images; every thumbnail is a plain color.
        val imageColor = MaterialTheme.colorScheme.secondaryContainer.toArgb()
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(imageColor) },
        ) {
            FavoritesScreen(
                uiState = FavoritesUiState(Content.Favorites(previewTeasers)),
                onOpenRecipe = {},
                onRemove = {},
                onRemovalMessageClosed = {},
                onRestoreFailureMessageClosed = {},
                onRemoveFailureMessageClosed = {},
            )
        }
    }
}

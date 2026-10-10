package io.github.fmweigl.theappthatgivesyourecipes.search.ui

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
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.SearchUiState.Content
import kotlinx.coroutines.flow.emptyFlow

/** Results written for previews (not from TheMealDB). */
private val previewResults = listOf(
    SearchResult("1", "Chicken katsu curry", "Chicken · Japanese", thumbnailUrl = null),
    SearchResult("2", "Chicken tikka masala", "Chicken · Indian", thumbnailUrl = null),
    SearchResult("3", "Chicken and leek pie", "Chicken · British", thumbnailUrl = null),
    SearchResult("4", "Chickpea and spinach stew", "Vegetarian · Spanish", thumbnailUrl = null),
)

@Composable
private fun PreviewScreen(uiState: SearchUiState) {
    MealsPreview {
        // Previews can't load images; every thumbnail is a plain color.
        val imageColor = MaterialTheme.colorScheme.secondaryContainer.toArgb()
        @OptIn(ExperimentalCoilApi::class)
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(imageColor) },
        ) {
            SearchScreen(
                uiState = uiState,
                onQueryChange = {},
                onSearch = {},
                onRetry = {},
                onOpenRecipe = {},
                tabReselected = emptyFlow(),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun EmptySearchScreenPreview() = PreviewScreen(SearchUiState())

@DevicePreviews
@Composable
private fun SearchScreenPreview() =
    PreviewScreen(SearchUiState(query = "chick", content = Content.Results(previewResults)))

@DevicePreviews
@Composable
private fun RefreshingSearchScreenPreview() =
    PreviewScreen(SearchUiState(query = "chicke", content = Content.Results(previewResults), isRefreshing = true))

@DevicePreviews
@Composable
private fun LoadingSearchScreenPreview() = PreviewScreen(SearchUiState(query = "chick", content = Content.Loading))

@DevicePreviews
@Composable
private fun NoMatchSearchScreenPreview() =
    PreviewScreen(SearchUiState(query = "xyzzy", content = Content.NoMatch("xyzzy")))

@DevicePreviews
@Composable
private fun ErrorSearchScreenPreview() =
    PreviewScreen(SearchUiState(query = "chick", content = Content.Error(DataError.NoConnection)))

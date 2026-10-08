package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fmweigl.yetanothermealsapp.favorites.ui.FavoritesUiState.Content
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.favorite_removed
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.favorite_restore_failed
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.favorites
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.favorites_error
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.loading
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.no_favorites
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.remove_favorite
import io.github.fmweigl.yetanothermealsapp.favorites.ui.resources.undo
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritesScreen(
    uiState: FavoritesUiState,
    onOpenRecipe: (recipeId: String) -> Unit,
    onRemove: (recipeId: String) -> Unit,
    onRemovalMessageClosed: (undo: Boolean) -> Unit,
    onRestoreFailureMessageClosed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    RemovalMessage(uiState.removed, snackbarHostState, onRemovalMessageClosed)
    RestoreFailureMessage(uiState.restoreFailed, snackbarHostState, onRestoreFailureMessageClosed)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.favorites), modifier = Modifier.semantics { heading() })
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            when (val content = uiState.content) {
                Content.Loading -> {
                    val loading = stringResource(Res.string.loading)
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center).semantics { contentDescription = loading },
                    )
                }
                Content.Empty -> EmptyMessage()
                Content.Error -> Message(Res.string.favorites_error, isError = true)
                is Content.Favorites -> FavoritesList(content.teasers, onOpenRecipe, onRemove)
            }
        }
    }
}

/** "Removed ‹name›" with "Undo" while [removed] is set; a new removal replaces the message. */
@Composable
private fun RemovalMessage(
    removed: RecipeTeaser?,
    snackbarHostState: SnackbarHostState,
    onClosed: (undo: Boolean) -> Unit,
) {
    LaunchedEffect(removed) {
        if (removed == null) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = getString(Res.string.favorite_removed, removed.name),
            actionLabel = getString(Res.string.undo),
            duration = SnackbarDuration.Long,
        )
        onClosed(result == SnackbarResult.ActionPerformed)
    }
}

/** "Couldn't restore ‹name›" while [failed] is set. */
@Composable
private fun RestoreFailureMessage(
    failed: RecipeTeaser?,
    snackbarHostState: SnackbarHostState,
    onClosed: () -> Unit,
) {
    LaunchedEffect(failed) {
        if (failed == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(
            message = getString(Res.string.favorite_restore_failed, failed.name),
            duration = SnackbarDuration.Long,
        )
        onClosed()
    }
}

/** Shown instead of the list; announced by screen readers when it appears (e.g. after removing the last favorite). */
@Composable
private fun Message(text: StringResource, isError: Boolean = false) {
    val message = stringResource(text)
    Text(
        message,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp).semantics { paneTitle = message },
    )
}

/** [EmptyFavoritesIllustration] above the "No favorites yet" text, announced like [Message]. */
@Composable
private fun EmptyMessage() {
    val message = stringResource(Res.string.no_favorites)
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).semantics { paneTitle = message },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        EmptyFavoritesIllustration()
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** Cards are at least this wide (at the default font size): one column on phones in portrait. */
private val MinCardWidth = 320.dp

private val ListPadding = 16.dp
private val CardSpacing = 12.dp

/**
 * As many columns as cards of [MinCardWidth] fit, e.g. one on phones and 7-inch tablets in
 * portrait, two on 10-inch tablets in portrait and larger phones in landscape, three on tablets in
 * landscape. Cards grow with the font size, so large text gets fewer, wider columns.
 *
 * The column count is computed here rather than with `GridCells.Adaptive` so the grid can tell
 * screen readers its size: a lazy grid, unlike a lazy column, reports no item count of its own.
 */
@Composable
private fun FavoritesList(
    teasers: List<RecipeTeaser>,
    onOpenRecipe: (recipeId: String) -> Unit,
    onRemove: (recipeId: String) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val minCardWidth = MinCardWidth * LocalDensity.current.fontScale.coerceAtLeast(1f)
        val columns = ((maxWidth - ListPadding * 2 + CardSpacing) / (minCardWidth + CardSpacing)).toInt()
            .coerceAtLeast(1)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize().semantics {
                val rows = (teasers.size + columns - 1) / columns
                collectionInfo = CollectionInfo(rowCount = rows, columnCount = columns)
            },
            contentPadding = PaddingValues(ListPadding),
            verticalArrangement = Arrangement.spacedBy(CardSpacing),
            horizontalArrangement = Arrangement.spacedBy(CardSpacing),
        ) {
            itemsIndexed(teasers, key = { _, teaser -> teaser.id }) { index, teaser ->
                TeaserCard(
                    teaser = teaser,
                    onClick = { onOpenRecipe(teaser.id) },
                    onRemove = { onRemove(teaser.id) },
                    modifier = Modifier.animateItem().semantics {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = index / columns,
                            rowSpan = 1,
                            columnIndex = index % columns,
                            columnSpan = 1,
                        )
                    },
                )
            }
        }
    }
}

/**
 * One element for screen readers ("‹name›, ‹category · area›"), with the remove button as a separate
 * one. The name takes at most two lines and the subtitle one (cut off with "…" if longer; screen
 * readers still get the whole text), and the text always gets room for all three lines, centered,
 * so every card is equally tall.
 */
@Composable
private fun TeaserCard(
    teaser: RecipeTeaser,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = teaser.imageUrl,
                // Decorative: the recipe's name is right next to it.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(MaterialTheme.shapes.medium),
            )
            val nameStyle = MaterialTheme.typography.titleMedium
            val subtitleStyle = MaterialTheme.typography.bodyMedium
            // Two lines of name and one of subtitle (in dp, so it follows the font size).
            val fixedTextHeight = with(LocalDensity.current) {
                (nameStyle.lineHeight * 2).toDp() + subtitleStyle.lineHeight.toDp()
            }
            Column(
                modifier = Modifier.weight(1f).heightIn(min = fixedTextHeight),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    teaser.name,
                    style = nameStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                teaser.subtitle?.let {
                    Text(
                        it,
                        style = subtitleStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.remove_favorite, teaser.name))
            }
        }
    }
}

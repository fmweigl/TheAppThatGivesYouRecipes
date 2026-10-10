package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.Skeleton
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component.SkeletonBlock
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.SearchUiState.Content
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.Res
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.loading
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.search
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.search_capped
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.search_hint
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.search_label
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.search_no_match
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.search_result_count
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.resources.try_again
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** On wide windows the field and the results keep this width, centered. */
private val MaxContentWidth = 640.dp

private val ContentPadding = 16.dp
private val ItemSpacing = 12.dp
private val ThumbnailSize = 72.dp
private const val SKELETON_ROWS = 8

/** Widths of the placeholder name and "category · area" line, as fractions of their space. */
private const val NAME_FRACTION = 0.7f
private const val SUBTITLE_FRACTION = 0.4f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onOpenRecipe: (recipeId: String) -> Unit,
    tabReselected: Flow<Unit>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    SearchEffects(listState, focusRequester, tabReselected)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.search), modifier = Modifier.semantics { heading() }) },
            )
        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = MaxContentWidth).fillMaxSize()) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ContentPadding)
                        .focusRequester(focusRequester),
                    label = { Text(stringResource(Res.string.search_label)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onSearch()
                        },
                    ),
                )
                RefreshIndicator(uiState.isRefreshing)
                when (val content = uiState.content) {
                    Content.Idle -> Message(stringResource(Res.string.search_hint), isError = false)
                    Content.Loading -> SearchSkeleton()
                    is Content.Results -> Results(content, listState, onOpenRecipe)
                    is Content.NoMatch ->
                        Message(stringResource(Res.string.search_no_match, content.query), isError = false)
                    is Content.Error -> ErrorBanner(content, onRetry)
                }
            }
        }
    }
}

@Composable
private fun SearchEffects(listState: LazyListState, focusRequester: FocusRequester, tabReselected: Flow<Unit>) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // Scrolling the results hides the keyboard.
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
            if (scrolling) {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        }
    }
    // Tapping the selected tab again: back to the top, ready to type.
    LaunchedEffect(tabReselected) {
        tabReselected.collect {
            listState.animateScrollToItem(0)
            focusRequester.requestFocus()
        }
    }
}

/** A thin bar while a search runs; its space is always reserved, so the results don't jump. */
@Composable
private fun RefreshIndicator(isRefreshing: Boolean) {
    val loading = stringResource(Res.string.loading)
    Box(Modifier.fillMaxWidth().height(4.dp).padding(horizontal = ContentPadding)) {
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = loading
                    progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                },
            )
        }
    }
}

/** A short text in the middle, announced by screen readers when it appears. */
@Composable
private fun Message(text: String, isError: Boolean) {
    Text(
        text,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp).semantics {
            paneTitle = text
            liveRegion = LiveRegionMode.Polite
        },
    )
}

@Composable
private fun Results(
    content: Content.Results,
    listState: LazyListState,
    onOpenRecipe: (recipeId: String) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        // A live region: screen readers announce the new count when the results change.
        Text(
            pluralStringResource(Res.plurals.search_result_count, content.results.size, content.results.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = ContentPadding, vertical = 4.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = ContentPadding, end = ContentPadding, bottom = ContentPadding),
            verticalArrangement = Arrangement.spacedBy(ItemSpacing),
        ) {
            items(content.results, key = { it.id }) { result ->
                ResultRow(result, onClick = { onOpenRecipe(result.id) })
            }
            if (content.isCapped) {
                item(key = "capped") {
                    Text(
                        stringResource(Res.string.search_capped, MAX_RESULTS),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

/**
 * One element for screen readers ("‹name›, ‹category · area›"). The name takes at most two lines,
 * the subtitle one (cut off with "…"; screen readers still get the whole text).
 */
@Composable
private fun ResultRow(result: SearchResult, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = result.thumbnailUrl,
                // Decorative: the recipe's name is right next to it.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(ThumbnailSize).clip(MaterialTheme.shapes.medium),
            )
            Column(Modifier.weight(1f).heightIn(min = ThumbnailSize), verticalArrangement = Arrangement.Center) {
                Text(
                    result.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                result.subtitle?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** Stands in for the first results: rows laid out like [ResultRow], one "Loading" element for screen readers. */
@Composable
private fun SearchSkeleton() {
    val loading = stringResource(Res.string.loading)
    Skeleton(
        Modifier.fillMaxSize().clearAndSetSemantics {
            contentDescription = loading
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
        },
    ) {
        Column(
            Modifier.padding(horizontal = ContentPadding),
            verticalArrangement = Arrangement.spacedBy(ItemSpacing),
        ) {
            repeat(SKELETON_ROWS) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SkeletonBlock(Modifier.size(ThumbnailSize), MaterialTheme.shapes.medium)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.fillMaxWidth(NAME_FRACTION).height(18.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(SUBTITLE_FRACTION).height(14.dp))
                    }
                }
            }
        }
    }
}

/** The error above where the results were, with a button to search again; announced when it appears. */
@Composable
private fun ErrorBanner(error: Content.Error, onRetry: () -> Unit) {
    val message = stringResource(error.error.toMessage())
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ContentPadding)
            .semantics { paneTitle = message },
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(message)
            Button(onClick = onRetry) { Text(stringResource(Res.string.try_again)) }
        }
    }
}

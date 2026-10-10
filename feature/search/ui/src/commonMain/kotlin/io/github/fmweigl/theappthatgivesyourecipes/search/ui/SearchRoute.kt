package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

/** Connects [SearchViewModel] to [SearchScreen]. */
@Composable
internal fun SearchRoute(
    onOpenRecipe: (recipeId: String) -> Unit,
    tabReselected: Flow<Unit>,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::search,
        onRetry = viewModel::retry,
        onOpenRecipe = onOpenRecipe,
        tabReselected = tabReselected,
        modifier = modifier,
    )
}

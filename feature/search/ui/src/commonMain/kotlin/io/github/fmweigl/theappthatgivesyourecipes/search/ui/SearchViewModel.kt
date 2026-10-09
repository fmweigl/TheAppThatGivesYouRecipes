package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.RecipeRepository
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.SearchUiState.Content
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/** Waits this long after the last keystroke before searching. */
internal const val DEBOUNCE_MILLIS = 300L

/** Shorter queries don't search. */
internal const val MIN_QUERY_LENGTH = 2

private const val QUERY_KEY = "query"

/**
 * Searches TheMealDB as the user types: the normalized query (trimmed, whitespace collapsed,
 * lowercase) is searched after a [DEBOUNCE_MILLIS] pause, and a newer query cancels an outdated
 * request, so old results never replace newer ones. [search] (the keyboard's Search action) and
 * [retry] search at once. The typed query is kept in [savedStateHandle], so it survives process death.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState(query = savedStateHandle[QUERY_KEY] ?: ""))
    val uiState: StateFlow<SearchUiState> = _uiState

    /** Searches that skip the debounce. */
    private val immediateSearches = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** The query of the latest search that started, so the debounced one doesn't repeat it. */
    private var latestQuery: String? = null

    init {
        merge(
            savedStateHandle.getStateFlow(QUERY_KEY, "")
                .map(::normalize)
                .debounce(DEBOUNCE_MILLIS)
                .distinctUntilChanged()
                .filter { it != latestQuery },
            immediateSearches,
        )
            .onEach { latestQuery = it }
            .mapLatest(::search)
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        savedStateHandle[QUERY_KEY] = query
        _uiState.update { it.copy(query = query) }
    }

    /** The keyboard's Search action: searches now instead of after the pause. */
    fun search() {
        immediateSearches.tryEmit(normalize(_uiState.value.query))
    }

    fun retry() = search()

    private suspend fun search(query: String) {
        if (query.length < MIN_QUERY_LENGTH) {
            _uiState.update { it.copy(content = Content.Idle, isRefreshing = false) }
            return
        }
        // Keep results (or the "no match" message) on screen while the next ones load.
        _uiState.update {
            when (it.content) {
                is Content.Results, is Content.NoMatch -> it.copy(isRefreshing = true)
                else -> it.copy(content = Content.Loading, isRefreshing = false)
            }
        }
        val content = when (val result = repository.searchRecipes(query)) {
            is Result.Success -> if (result.data.isEmpty()) {
                Content.NoMatch(query)
            } else {
                Content.Results(result.data.map { it.toResult() })
            }
            is Result.Failure -> Content.Error(result.error)
        }
        _uiState.update { it.copy(content = content, isRefreshing = false) }
    }
}

/** Trimmed, whitespace collapsed to single spaces, lowercase. */
internal fun normalize(query: String): String =
    query.trim().replace(WHITESPACE, " ").lowercase()

private val WHITESPACE = Regex("\\s+")

private fun Recipe.toResult() = SearchResult(
    id = id,
    name = name.trim(),
    subtitle = listOfNotNull(category, area).joinToString(" · ").ifEmpty { null },
    // TheMealDB serves smaller copies of its images under /small.
    thumbnailUrl = imageUrl?.let { "$it/small" },
)

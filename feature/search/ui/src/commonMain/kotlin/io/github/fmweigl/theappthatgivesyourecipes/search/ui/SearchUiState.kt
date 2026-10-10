package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError

/** TheMealDB answers a search with at most this many recipes and has no further pages. */
internal const val MAX_RESULTS = 25

/**
 * @property query what the search field shows, as typed.
 * @property isRefreshing a search runs while [content] still shows the previous results.
 */
internal data class SearchUiState(
    val query: String = "",
    val content: Content = Content.Idle,
    val isRefreshing: Boolean = false,
) {
    sealed interface Content {
        /** No search yet: the query is shorter than the minimum. */
        data object Idle : Content

        /** The first search runs; there are no previous results to keep. */
        data object Loading : Content

        data class Results(val results: List<SearchResult>) : Content {
            /** The API stops at [MAX_RESULTS], so there may be more recipes with this name. */
            val isCapped: Boolean get() = results.size >= MAX_RESULTS
        }

        /** Nothing matches [query] (normalized). */
        data class NoMatch(val query: String) : Content

        data class Error(val error: DataError) : Content
    }
}

/** One row of the results. */
internal data class SearchResult(
    val id: String,
    val name: String,
    val subtitle: String?,
    val thumbnailUrl: String?,
)

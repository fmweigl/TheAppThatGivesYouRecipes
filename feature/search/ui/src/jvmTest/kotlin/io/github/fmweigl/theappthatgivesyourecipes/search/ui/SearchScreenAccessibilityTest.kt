package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runPhoneTest
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runTabletLandscapeTest
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runTabletPortraitTest
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.SearchUiState.Content
import kotlinx.coroutines.flow.emptyFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** What screen readers get from [SearchScreen] (the merged semantics tree), at several window sizes. */
@OptIn(ExperimentalTestApi::class)
class SearchScreenAccessibilityTest {

    private val results = listOf(
        SearchResult("1", "Chicken curry", "Chicken · Indian", thumbnailUrl = null),
        SearchResult("2", "Chicken soup", null, thumbnailUrl = null),
    )

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private val isPoliteLiveRegion =
        SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite)

    private val isLoadingIndicator = hasContentDescription("Loading") and
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate)

    private fun paneTitle(title: String) = SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, title)

    @Composable
    private fun Screen(uiState: SearchUiState, onRetry: () -> Unit = {}, onOpen: (String) -> Unit = {}) {
        SearchScreen(
            uiState = uiState,
            onQueryChange = {},
            onSearch = {},
            onRetry = onRetry,
            onOpenRecipe = onOpen,
            tabReselected = emptyFlow(),
        )
    }

    private fun ComposeUiTest.assertSearchFieldIsLabeled() {
        onNode(hasSetTextAction()).assert(hasText("Search recipes by name"))
    }

    @Test
    fun theSearchFieldHasALabelOnPhones() = runPhoneTest {
        setContent { Screen(SearchUiState()) }

        assertSearchFieldIsLabeled()
        onNodeWithText("Search").assert(isHeading)
    }

    @Test
    fun theSearchFieldHasALabelOnTabletsInPortrait() = runTabletPortraitTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Results(results))) }

        assertSearchFieldIsLabeled()
    }

    @Test
    fun theSearchFieldHasALabelOnTabletsInLandscape() = runTabletLandscapeTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Results(results))) }

        assertSearchFieldIsLabeled()
    }

    @Test
    fun theHintIsAnnouncedWithoutAQuery() = runPhoneTest {
        setContent { Screen(SearchUiState()) }

        onNode(paneTitle("Search recipes by name")).assertExists()
    }

    @Test
    fun aLiveRegionAnnouncesTheResultCount() = runPhoneTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Results(results))) }

        onNodeWithText("2 recipes found").assert(isPoliteLiveRegion)
    }

    @Test
    fun theResultCountIsSingularForOneRecipe() = runPhoneTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Results(results.take(1)))) }

        onNodeWithText("1 recipe found").assert(isPoliteLiveRegion)
    }

    @Test
    fun eachRowReadsAsOneItem() = runPhoneTest {
        var opened: String? = null
        setContent {
            Screen(SearchUiState(query = "chick", content = Content.Results(results)), onOpen = { opened = it })
        }

        // Name and "category · area" are merged into the row's one element, which is clickable.
        onNode(hasText("Chicken curry") and hasText("Chicken · Indian")).assertHasClickAction()
        onAllNodesWithText("Chicken curry").assertCountEquals(1)
        onAllNodesWithText("Chicken · Indian").assertCountEquals(1)
        onNode(hasText("Chicken soup")).assertHasClickAction()
        onNode(hasText("Chicken curry")).performClick()
        assertEquals("1", opened)
    }

    @Test
    fun theCappedFooterAppearsAtTheLimit() = runPhoneTest {
        val many = (1..MAX_RESULTS).map { SearchResult("$it", "Soup $it", null, thumbnailUrl = null) }
        setContent { Screen(SearchUiState(query = "soup", content = Content.Results(many))) }

        onNodeWithText("$MAX_RESULTS recipes found").assert(isPoliteLiveRegion)
        val footer = hasText("Showing the first 25 — add more of the name to narrow it down")
        onNode(hasScrollAction()).performScrollToNode(footer)
        onNode(footer).assertExists()
    }

    @Test
    fun noFooterBelowTheLimit() = runPhoneTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Results(results))) }

        onAllNodesWithText("Showing the first", substring = true).assertCountEquals(0)
    }

    @Test
    fun theSkeletonIsOneProgressElementDescribedAsLoading() = runPhoneTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Loading)) }

        onAllNodesWithText("Loading").assertCountEquals(0)
        onNode(isLoadingIndicator).assertExists()
    }

    @Test
    fun refreshingKeepsTheResultsAndShowsAProgressBar() = runPhoneTest {
        setContent { Screen(SearchUiState(query = "chick", content = Content.Results(results), isRefreshing = true)) }

        onNodeWithText("Chicken curry").assertExists()
        onNode(isLoadingIndicator).assertExists()
    }

    @Test
    fun noMatchNamesTheQuery() = runPhoneTest {
        setContent { Screen(SearchUiState(query = "xyzzy", content = Content.NoMatch("xyzzy"))) }

        onNodeWithText("No recipes called ‘xyzzy’").assert(paneTitle("No recipes called ‘xyzzy’"))
    }

    @Test
    fun theErrorIsAnnouncedWithRetry() = runPhoneTest {
        var retried = false
        setContent {
            Screen(
                SearchUiState(query = "chick", content = Content.Error(DataError.NoConnection)),
                onRetry = { retried = true },
            )
        }

        val message = "Could not reach TheMealDB. Check your connection."
        onNode(paneTitle(message)).assertExists()
        onNodeWithText(message).assertExists()
        onNodeWithText("Try again").assertHasClickAction().performClick()
        assertTrue(retried)
    }
}

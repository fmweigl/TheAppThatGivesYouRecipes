package io.github.fmweigl.yetanothermealsapp.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.fmweigl.yetanothermealsapp.favorites.ui.FavoritesUiState.Content
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What screen readers get from [FavoritesScreen] (the merged semantics tree). */
@OptIn(ExperimentalTestApi::class)
class FavoritesScreenAccessibilityTest {

    private val tarts = RecipeTeaser("52923", "Canadian Butter Tarts", "Dessert · Canadian", imageUrl = null)

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private val isPoliteLiveRegion =
        SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite)

    private fun paneTitle(title: String) = SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, title)

    @Test
    fun titleIsHeading() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Favorites(listOf(tarts)))) }

        onNodeWithText("Favorites").assert(isHeading)
    }

    @Test
    fun eachTeaserIsOneClickableElementWithNameAndSubtitle() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Favorites(listOf(tarts)))) }

        onAllNodesWithText(tarts.name).assertCountEquals(1)
        onNodeWithText(tarts.name).assert(hasText("Dessert · Canadian")).assertHasClickAction()
    }

    @Test
    fun removeButtonNamesTheRecipe() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Favorites(listOf(tarts)))) }

        onNodeWithContentDescription("Remove Canadian Butter Tarts from favorites").assertHasClickAction()
    }

    @Test
    fun emptyStateIsAnnounced() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Empty)) }

        onNode(paneTitle("No favorites yet. Tap the heart on a recipe to save it.")).assertExists()
    }

    @Test
    fun errorIsAnnounced() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Error)) }

        onNode(paneTitle("Your favorites could not be loaded from this device.")).assertExists()
    }

    @Test
    fun loadingIndicatorIsDescribed() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Loading)) }

        onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun removalMessageOffersUndo() = runComposeUiTest {
        var closedWithUndo: Boolean? = null
        setContent {
            Screen(FavoritesUiState(Content.Empty, removed = tarts), onRemovalMessageClosed = { closedWithUndo = it })
        }

        onNodeWithText("Removed Canadian Butter Tarts").assertExists()
        onNodeWithText("Undo").performClick()
        waitForIdle()
        assertEquals(true, closedWithUndo)
    }

    @Test
    fun failedRestoreIsReportedPolitelyAndClosesAfterTheTimeout() = runComposeUiTest {
        var closed = false
        setContent {
            Screen(
                FavoritesUiState(Content.Empty, restoreFailed = tarts),
                onRestoreFailureMessageClosed = { closed = true },
            )
        }

        val message = "Couldn't restore Canadian Butter Tarts"
        onNodeWithText(message).assertExists()
        onNode(hasText(message) and hasAnyAncestor(isPoliteLiveRegion)).assertExists()
        assertFalse(closed)

        mainClock.advanceTimeBy(15_000)
        waitForIdle()
        assertTrue(closed)
    }

    @Composable
    private fun Screen(
        uiState: FavoritesUiState,
        onRemovalMessageClosed: (Boolean) -> Unit = {},
        onRestoreFailureMessageClosed: () -> Unit = {},
    ) {
        FavoritesScreen(
            uiState = uiState,
            onOpenRecipe = {},
            onRemove = {},
            onRemovalMessageClosed = onRemovalMessageClosed,
            onRestoreFailureMessageClosed = onRestoreFailureMessageClosed,
        )
    }
}

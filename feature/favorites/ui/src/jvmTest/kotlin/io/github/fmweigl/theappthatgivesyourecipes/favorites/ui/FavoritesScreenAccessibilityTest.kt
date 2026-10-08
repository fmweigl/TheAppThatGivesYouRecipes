package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.github.fmweigl.theappthatgivesyourecipes.favorites.ui.FavoritesUiState.Content
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

    private fun collection(rows: Int, columns: Int) = SemanticsMatcher("$rows×$columns collection") {
        val info = it.config.getOrNull(SemanticsProperties.CollectionInfo)
        info?.rowCount == rows && info.columnCount == columns
    }

    private fun collectionItem(row: Int, column: Int) = SemanticsMatcher("collection item ($row, $column)") {
        val info = it.config.getOrNull(SemanticsProperties.CollectionItemInfo)
        info?.rowIndex == row && info.columnIndex == column
    }

    private fun paneTitle(title: String) = SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, title)

    private val teasers = listOf(
        tarts,
        RecipeTeaser("1", "Shakshuka", "Vegetarian · Tunisian", imageUrl = null),
        RecipeTeaser("2", "Spaghetti al pomodoro", "Pasta · Italian", imageUrl = null),
    )

    @Test
    fun phoneStacksTheCards() = runDesktopComposeUiTest(width = 411, height = 891) {
        setContent { Screen(FavoritesUiState(Content.Favorites(teasers))) }

        val tops = teasers.map { onNodeWithText(it.name).fetchSemanticsNode().boundsInRoot.top }
        assertEquals(tops.sorted(), tops)
        assertEquals(teasers.size, tops.distinct().size)
    }

    @Test
    fun tabletInLandscapeShowsThreeCardsPerRowInReadingOrder() = runDesktopComposeUiTest(width = 1280, height = 800) {
        setContent { Screen(FavoritesUiState(Content.Favorites(teasers))) }

        val bounds = teasers.map { onNodeWithText(it.name).fetchSemanticsNode().boundsInRoot }
        assertEquals(1, bounds.map { it.top }.distinct().size)
        assertEquals(bounds.sortedBy { it.left }, bounds)
    }

    @Test
    fun gridTellsScreenReadersItsSize() = runDesktopComposeUiTest(width = 1280, height = 800) {
        val more = teasers.map { it.copy(id = it.id + "b", name = it.name + " II") }
        setContent { Screen(FavoritesUiState(Content.Favorites(teasers + more))) }

        onNode(collection(rows = 2, columns = 3)).assertExists()
        onNodeWithText("Shakshuka").assert(collectionItem(row = 0, column = 1))
        onNodeWithText("Shakshuka II").assert(collectionItem(row = 1, column = 1))
    }

    @Test
    fun phoneListTellsScreenReadersItsSize() = runDesktopComposeUiTest(width = 411, height = 891) {
        setContent { Screen(FavoritesUiState(Content.Favorites(teasers))) }

        onNode(collection(rows = 3, columns = 1)).assertExists()
    }

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
    fun removeButtonIsAPlainButtonNotAToggle() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Favorites(listOf(tarts)))) }

        onNodeWithContentDescription("Remove Canadian Butter Tarts from favorites")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ToggleableState))
    }

    @Test
    fun removeButtonIsSeparateFromTheCard() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Favorites(listOf(tarts)))) }

        val label = "Remove Canadian Butter Tarts from favorites"
        onNodeWithText(tarts.name).assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
        onNodeWithContentDescription(label).assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Text))
    }

    @Test
    fun removeButtonWorksWithTheKeyboard() = runDesktopComposeUiTest {
        var removedId: String? = null
        var opened = false
        setContent {
            Screen(
                FavoritesUiState(Content.Favorites(listOf(tarts))),
                onOpenRecipe = { opened = true },
                onRemove = { removedId = it },
            )
        }

        // The card, then its heart.
        onRoot().performKeyInput {
            pressKey(Key.Tab)
            pressKey(Key.Tab)
            pressKey(Key.Enter)
        }
        waitForIdle()
        assertEquals(tarts.id, removedId)
        assertFalse(opened)
    }

    @Test
    fun failedRemoveIsReported() = runComposeUiTest {
        var closed = false
        setContent {
            Screen(
                FavoritesUiState(Content.Favorites(listOf(tarts)), removeFailed = tarts),
                onRemoveFailureMessageClosed = { closed = true },
            )
        }

        onNodeWithText("Couldn't remove Canadian Butter Tarts").assertExists()
        mainClock.advanceTimeBy(15_000)
        waitForIdle()
        assertTrue(closed)
    }

    @Test
    fun emptyStateIsAnnounced() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Empty)) }

        onNode(paneTitle("No favorites yet. Tap the heart on a recipe to save it.")).assertExists()
    }

    private val emptyMessage = "No favorites yet. Tap the heart on a recipe to save it."

    @Test
    fun emptyStateReadsOnlyItsMessage() = runComposeUiTest {
        setContent { Screen(FavoritesUiState(Content.Empty)) }

        // Inside the empty state, the message is the only thing with text or a description:
        // the illustration is decorative.
        val readable = SemanticsMatcher("has text or a description") {
            it.config.contains(SemanticsProperties.Text) || it.config.contains(SemanticsProperties.ContentDescription)
        }
        onAllNodes(hasAnyAncestor(paneTitle(emptyMessage)) and readable, useUnmergedTree = true)
            .assertCountEquals(1)
            .onFirst()
            .assert(hasText(emptyMessage))
    }

    @Test
    fun emptyStateTextStaysReachableInAShortWindow() = runDesktopComposeUiTest(width = 640, height = 220) {
        setContent { Screen(FavoritesUiState(Content.Empty)) }

        onNodeWithText(emptyMessage).performScrollTo().assertIsDisplayed()
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
        onOpenRecipe: (String) -> Unit = {},
        onRemove: (String) -> Unit = {},
        onRemovalMessageClosed: (Boolean) -> Unit = {},
        onRestoreFailureMessageClosed: () -> Unit = {},
        onRemoveFailureMessageClosed: () -> Unit = {},
    ) {
        FavoritesScreen(
            uiState = uiState,
            onOpenRecipe = onOpenRecipe,
            onRemove = onRemove,
            onRemovalMessageClosed = onRemovalMessageClosed,
            onRestoreFailureMessageClosed = onRestoreFailureMessageClosed,
            onRemoveFailureMessageClosed = onRemoveFailureMessageClosed,
        )
    }
}

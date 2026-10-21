package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runPhoneTest
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runTabletLandscapeTest
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.CONTENT_FADE_MILLIS
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.LocalSharer
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.ShareRequest
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.Sharer
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail.RecipeUiState.Content
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** What screen readers get from [RecipeScreen] (the merged semantics tree). */
@OptIn(ExperimentalTestApi::class)
class RecipeScreenAccessibilityTest {

    private val recipe = Recipe(
        id = "52923",
        name = "Canadian Butter Tarts",
        category = "Dessert",
        instructions = "Bake the tarts.",
        imageUrl = null,
        ingredients = listOf(Ingredient("Butter", "50g"), Ingredient("Brown sugar", "100g")),
    )

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private val isLoadingIndicator = hasContentDescription("Loading") and
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate)

    private val isPoliteLiveRegion =
        SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite)

    private fun paneTitle(title: String) = SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, title)

    @Test
    fun topBarTitleAndRecipeNameAreHeadingsReadOnceEach() = runPhoneTest {
        setContent { Screen(Content.Success(recipe)) }

        onNodeWithText("Recipe").assert(isHeading)
        scrollTo(recipe.name)
        onAllNodesWithText(recipe.name).assertCountEquals(1)
        onNodeWithText(recipe.name).assert(isHeading)
    }

    @Test
    fun backButtonIsLabeled() = runPhoneTest {
        setContent { Screen(Content.Loading) }

        onNodeWithContentDescription("Back").assertHasClickAction()
    }

    @Test
    fun favoriteButtonReportsItsState() = runPhoneTest {
        setContent { Screen(Content.Success(recipe), isFavorite = true) }

        scrollTo(recipe.name)
        onNodeWithContentDescription("Favorite").assertIsOn()
    }

    @Test
    fun copyingIsConfirmedAndAnnounced() = runPhoneTest {
        val shared = mutableListOf<ShareRequest>()
        setContent { Screen(Content.Success(recipe), sharer = { shared += it }) }
        mainClock.autoAdvance = false

        scrollTo(recipe.name)
        onNodeWithContentDescription("Copy recipe").performClick()
        mainClock.advanceTimeByFrame()

        assertEquals(recipe.name, shared.single().subject)
        assertTrue(shared.single().text.startsWith("Canadian Butter Tarts\nDessert\n\nIngredients\n• 50g Butter"))
        onNodeWithContentDescription("Recipe copied").assert(isPoliteLiveRegion)
        mainClock.advanceTimeBy(3_000)
        onAllNodesWithContentDescription("Recipe copied").assertCountEquals(0)
    }

    @Test
    fun shareButtonWorksWithTheKeyboard() = runPhoneTest {
        val shared = mutableListOf<ShareRequest>()
        setContent { Screen(Content.Success(recipe), sharer = { shared += it }) }

        scrollTo(recipe.name)
        val button = onNodeWithContentDescription("Copy recipe")
        button.requestFocus()
        button.performKeyInput { pressKey(Key.Enter) }

        assertEquals(1, shared.size)
    }

    @Test
    fun errorIsAnnouncedWithRetry() = runPhoneTest {
        setContent { Screen(Content.Error(DataError.NoConnection)) }

        onNode(paneTitle("Could not reach TheMealDB. Check your connection.")).assertExists()
        onNodeWithText("Try again").assertHasClickAction()
    }

    @Test
    fun notFoundIsAnnouncedWithoutRetry() = runPhoneTest {
        setContent { Screen(Content.Error(DataError.NotFound)) }

        onNode(paneTitle("This recipe could not be found on TheMealDB.")).assertExists()
        onAllNodesWithText("Try again").assertCountEquals(0)
    }

    @Test
    fun skeletonIsOneProgressElementDescribedAsLoading() = runPhoneTest {
        setContent { Screen(Content.Loading) }

        onAllNodes(isLoadingIndicator).assertCountEquals(1)
        onAllNodes(hasScrollAction()).assertCountEquals(0)
    }

    /** As after "Try again": only the incoming content is there for screen readers, also during the fade. */
    @Test
    fun errorSkeletonAndRecipeFadeIntoEachOther() = runPhoneTest {
        var content: Content by mutableStateOf(Content.Error(DataError.NoConnection))
        setContent { Screen(content) }
        mainClock.autoAdvance = false

        content = Content.Loading
        mainClock.advanceTimeBy(CONTENT_FADE_MILLIS / 2L)
        onAllNodesWithText("Try again").assertCountEquals(0)
        onAllNodes(isLoadingIndicator).assertCountEquals(1)

        content = Content.Success(recipe)
        mainClock.advanceTimeBy(CONTENT_FADE_MILLIS / 2L)
        onAllNodes(isLoadingIndicator).assertCountEquals(0)
        onNode(paneTitle(recipe.name)).assertExists()
    }

    @Test
    fun twoPanesKeepHeadings() = runTabletLandscapeTest {
        setContent { Screen(Content.Success(recipe), isFavorite = true) }

        onNodeWithText("Recipe").assert(isHeading)
        onAllNodesWithText(recipe.name).assertCountEquals(1)
        onNodeWithText(recipe.name).assert(isHeading)
        onNodeWithText("Ingredients").assert(isHeading)
        onNodeWithText("Instructions").assert(isHeading)
        onNodeWithText("Butter").assert(hasText("50g"))
        onNodeWithContentDescription("Favorite").assertIsOn()
        onNodeWithContentDescription("Copy recipe").assertHasClickAction()
    }

    @Test
    fun tagsAreOneTextWithoutFocusOrClickOnPhone() = runPhoneTest {
        setContent { Screen(Content.Success(recipe.copy(tags = listOf("Spicy", "Breakfast", "spicy")))) }

        scrollTo("Tags: Spicy, Breakfast")
        onAllNodesWithText("Tags: Spicy, Breakfast").assertCountEquals(1)
        onNodeWithText("Tags: Spicy, Breakfast")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Focused))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
    }

    @Test
    fun tagsAreOneTextInTwoPanes() = runTabletLandscapeTest {
        setContent { Screen(Content.Success(recipe.copy(tags = listOf("Spicy", "Breakfast")))) }

        onAllNodesWithText("Tags: Spicy, Breakfast").assertCountEquals(1)
        onNodeWithText("Tags: Spicy, Breakfast")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
    }

    @Test
    fun noTagsShowNothing() = runPhoneTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo(recipe.name)
        onAllNodes(hasText("Tags", substring = true)).assertCountEquals(0)
    }

    /** The recipe's image fills the test window, so the lazy list only composes what's scrolled to. */
    private fun ComposeUiTest.scrollTo(text: String) {
        onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    @Composable
    private fun Screen(content: Content, isFavorite: Boolean = false, sharer: Sharer = Sharer {}) {
        CompositionLocalProvider(LocalSharer provides sharer) {
            RecipeScreen(
                uiState = RecipeUiState(content = content, isFavorite = isFavorite),
                onBack = {},
                onRetry = {},
                onToggleFavorite = {},
            )
        }
    }
}

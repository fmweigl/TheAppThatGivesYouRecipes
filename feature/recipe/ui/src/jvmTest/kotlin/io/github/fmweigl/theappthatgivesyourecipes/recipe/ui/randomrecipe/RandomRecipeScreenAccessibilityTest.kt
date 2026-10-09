package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component.CONTENT_FADE_MILLIS
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.randomrecipe.RandomRecipeUiState.Content
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.runPhoneTest
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.runSmallPhoneLandscapeTest
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.runTabletLandscapeTest
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.runTabletPortraitTest
import kotlin.test.Test

/** Points in the fade between the screen's contents. */
private const val FADE_HALFWAY_MILLIS = CONTENT_FADE_MILLIS / 2L
private const val FADE_DONE_MILLIS = CONTENT_FADE_MILLIS * 2L

/** What screen readers get from [RandomRecipeScreen] (the merged semantics tree). */
@OptIn(ExperimentalTestApi::class)
class RandomRecipeScreenAccessibilityTest {

    private val recipe = Recipe(
        id = "1",
        name = "Fettucine alfredo",
        category = "Pasta",
        area = "Italian",
        instructions = "Cook the pasta.",
        imageUrl = null,
        ingredients = listOf(Ingredient("Clotted Cream", "227g"), Ingredient("Butter", "25g")),
    )

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private val isLoadingIndicator = hasContentDescription("Loading") and
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate)

    private fun paneTitle(title: String) = SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, title)

    @Test
    fun recipeNameIsReadOnceAsHeading() = runPhoneTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo(recipe.name)
        onAllNodesWithText(recipe.name).assertCountEquals(1)
        onAllNodesWithContentDescription(recipe.name).assertCountEquals(0)
        onNodeWithText(recipe.name).assert(isHeading)
    }

    @Test
    fun sectionTitlesAreHeadings() = runPhoneTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo("Ingredients")
        onNodeWithText("Ingredients").assert(isHeading)
        scrollTo("Instructions")
        onNodeWithText("Instructions").assert(isHeading)
    }

    @Test
    fun eachIngredientIsOneElement() = runPhoneTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo("Clotted Cream")
        onAllNodesWithText("Clotted Cream").assertCountEquals(1)
        onNodeWithText("Clotted Cream").assert(hasText("227g"))
    }

    @Test
    fun favoriteButtonIsLabeledAndReportsItsState() = runPhoneTest {
        var isFavorite by mutableStateOf(false)
        setContent { Screen(Content.Success(recipe), isFavorite = isFavorite) }

        scrollTo(recipe.name)
        onNodeWithContentDescription("Favorite").assertIsOff()
        isFavorite = true
        onNodeWithContentDescription("Favorite").assertIsOn()
    }

    @Test
    fun recipeIsAnnouncedWhenItAppears() = runPhoneTest {
        setContent { Screen(Content.Success(recipe)) }

        onNode(paneTitle(recipe.name)).assertExists()
    }

    @Test
    fun errorIsAnnouncedWhenItAppears() = runPhoneTest {
        setContent { Screen(Content.Error(DataError.NoConnection)) }

        onNode(paneTitle("Could not reach TheMealDB. Check your connection.")).assertExists()
    }

    @Test
    fun skeletonIsOneProgressElementDescribedAsLoading() = runPhoneTest {
        setContent { Screen(Content.Loading) }

        onAllNodes(isLoadingIndicator).assertCountEquals(1)
        // Nothing to scroll to yet: the placeholders aren't content.
        onAllNodes(hasScrollAction()).assertCountEquals(0)
    }

    @Test
    fun centeredColumnSkeletonIsOneProgressElement() = runTabletPortraitTest {
        setContent { Screen(Content.Loading) }

        onAllNodes(isLoadingIndicator).assertCountEquals(1)
        onAllNodes(hasScrollAction()).assertCountEquals(0)
    }

    @Test
    fun twoPaneSkeletonIsOneProgressElement() = runTabletLandscapeTest {
        setContent { Screen(Content.Loading) }

        onAllNodes(isLoadingIndicator).assertCountEquals(1)
        onAllNodes(hasScrollAction()).assertCountEquals(0)
    }

    /** During the fade only the incoming content is there for screen readers; afterwards too. */
    @Test
    fun skeletonAndRecipeFadeIntoEachOther() = runPhoneTest {
        var content: Content by mutableStateOf(Content.Loading)
        setContent { Screen(content) }
        mainClock.autoAdvance = false

        content = Content.Success(recipe)
        mainClock.advanceTimeBy(FADE_HALFWAY_MILLIS)
        onAllNodes(isLoadingIndicator).assertCountEquals(0)
        onNode(paneTitle(recipe.name)).assertExists()

        mainClock.advanceTimeBy(FADE_DONE_MILLIS)
        onAllNodes(isLoadingIndicator).assertCountEquals(0)
        onNode(paneTitle(recipe.name)).assertExists()

        content = Content.Loading
        mainClock.advanceTimeBy(FADE_HALFWAY_MILLIS)
        onAllNodes(isLoadingIndicator).assertCountEquals(1)
        onAllNodes(paneTitle(recipe.name)).assertCountEquals(0)
    }

    /** As with "Previous": the leaving recipe's heart is gone at once, so it can't change the new one. */
    @Test
    fun leavingRecipeIsGoneForScreenReadersDuringTheFade() = runTabletLandscapeTest {
        val other = recipe.copy(id = "2", name = "Spaghetti alla puttanesca")
        var content: Content by mutableStateOf(Content.Success(recipe))
        setContent { Screen(content) }
        mainClock.autoAdvance = false

        content = Content.Success(other)
        mainClock.advanceTimeBy(FADE_HALFWAY_MILLIS)

        onAllNodes(paneTitle(recipe.name)).assertCountEquals(0)
        onAllNodesWithText(recipe.name).assertCountEquals(0)
        onNode(paneTitle(other.name)).assertExists()
        onAllNodesWithContentDescription("Favorite").assertCountEquals(1)
    }

    /** A changed heart updates the recipe in place: no fade, still one recipe. */
    @Test
    fun favoriteChangeDoesNotFade() = runTabletLandscapeTest {
        var isFavorite by mutableStateOf(false)
        setContent { Screen(Content.Success(recipe), isFavorite = isFavorite) }
        mainClock.autoAdvance = false

        isFavorite = true
        mainClock.advanceTimeBy(FADE_HALFWAY_MILLIS)

        onAllNodes(paneTitle(recipe.name)).assertCountEquals(1)
        onNodeWithContentDescription("Favorite").assertIsOn()
    }

    @Test
    fun twoPanesKeepHeadingsAndIngredients() = runTabletLandscapeTest {
        setContent { Screen(Content.Success(recipe)) }

        onNode(paneTitle(recipe.name)).assertExists()
        onAllNodesWithText(recipe.name).assertCountEquals(1)
        onNodeWithText(recipe.name).assert(isHeading)
        onNodeWithText("Ingredients").assert(isHeading)
        onNodeWithText("Instructions").assert(isHeading)
        onNodeWithText("Clotted Cream").assert(hasText("227g"))
        onNodeWithContentDescription("Favorite").assertIsOff()
    }

    @Test
    fun centeredColumnKeepsHeadingsAndIngredients() = runTabletPortraitTest {
        setContent { Screen(Content.Success(recipe)) }

        onNode(paneTitle(recipe.name)).assertExists()
        scrollTo(recipe.name)
        onAllNodesWithText(recipe.name).assertCountEquals(1)
        onNodeWithText(recipe.name).assert(isHeading)
        scrollTo("Ingredients")
        onNodeWithText("Ingredients").assert(isHeading)
        scrollTo("Clotted Cream")
        onNodeWithText("Clotted Cream").assert(hasText("227g"))
    }

    @Test
    fun shortWindowShowsNameAndFavoriteWithoutScrolling() = runSmallPhoneLandscapeTest {
        setContent { Screen(Content.Success(recipe)) }

        onNodeWithText(recipe.name).assertIsDisplayed()
        onNodeWithContentDescription("Favorite").assertIsDisplayed()
    }

    @Test
    fun ingredientsPaneScrollsWithTheKeyboard() = runTabletLandscapeTest {
        val longRecipe = recipe.copy(ingredients = List(40) { Ingredient("Ingredient $it", "$it g") })
        setContent { Screen(Content.Success(longRecipe)) }
        onNodeWithText("Instructions").assertDoesNotExist()

        val ingredientsPane = onNode(hasScrollAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.Focused))
        ingredientsPane.requestFocus()
        repeat(times = 6) { ingredientsPane.performKeyInput { pressKey(Key.PageDown) } }

        onNodeWithText("Instructions").assertIsDisplayed()
    }

    /** The recipe's image fills the test window, so the lazy list only composes what's scrolled to. */
    private fun ComposeUiTest.scrollTo(text: String) {
        onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    @Composable
    private fun Screen(content: Content, isFavorite: Boolean = false) {
        RandomRecipeScreen(
            uiState = RandomRecipeUiState(content = content, isFavorite = isFavorite),
            onShowNext = {},
            onShowPrevious = {},
            onToggleFavorite = {},
        )
    }
}

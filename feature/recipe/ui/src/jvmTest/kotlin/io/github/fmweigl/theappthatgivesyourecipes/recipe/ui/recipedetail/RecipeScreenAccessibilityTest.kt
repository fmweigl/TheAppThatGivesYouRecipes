package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Ingredient
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.recipedetail.RecipeUiState.Content
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.runPhoneTest
import io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.runTabletLandscapeTest
import kotlin.test.Test

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
    fun loadingIndicatorIsDescribed() = runPhoneTest {
        setContent { Screen(Content.Loading) }

        onNodeWithContentDescription("Loading").assertExists()
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
    }

    /** The recipe's image fills the test window, so the lazy list only composes what's scrolled to. */
    private fun ComposeUiTest.scrollTo(text: String) {
        onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    @Composable
    private fun Screen(content: Content, isFavorite: Boolean = false) {
        RecipeScreen(
            uiState = RecipeUiState(content = content, isFavorite = isFavorite),
            onBack = {},
            onRetry = {},
            onToggleFavorite = {},
        )
    }
}

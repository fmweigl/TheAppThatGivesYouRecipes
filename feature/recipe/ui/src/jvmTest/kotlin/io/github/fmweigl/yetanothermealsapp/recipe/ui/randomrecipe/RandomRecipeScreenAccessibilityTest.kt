package io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Ingredient
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe.RandomRecipeUiState.Content
import kotlin.test.Test

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

    private fun paneTitle(title: String) = SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, title)

    @Test
    fun recipeNameIsReadOnceAsHeading() = runComposeUiTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo(recipe.name)
        onAllNodesWithText(recipe.name).assertCountEquals(1)
        onAllNodesWithContentDescription(recipe.name).assertCountEquals(0)
        onNodeWithText(recipe.name).assert(isHeading)
    }

    @Test
    fun sectionTitlesAreHeadings() = runComposeUiTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo("Ingredients")
        onNodeWithText("Ingredients").assert(isHeading)
        scrollTo("Instructions")
        onNodeWithText("Instructions").assert(isHeading)
    }

    @Test
    fun eachIngredientIsOneElement() = runComposeUiTest {
        setContent { Screen(Content.Success(recipe)) }

        scrollTo("Clotted Cream")
        onAllNodesWithText("Clotted Cream").assertCountEquals(1)
        onNodeWithText("Clotted Cream").assert(hasText("227g"))
    }

    @Test
    fun favoriteButtonIsLabeledAndReportsItsState() = runComposeUiTest {
        var isFavorite by mutableStateOf(false)
        setContent { Screen(Content.Success(recipe), isFavorite = isFavorite) }

        scrollTo(recipe.name)
        onNodeWithContentDescription("Favorite").assertIsOff()
        isFavorite = true
        onNodeWithContentDescription("Favorite").assertIsOn()
    }

    @Test
    fun recipeIsAnnouncedWhenItAppears() = runComposeUiTest {
        setContent { Screen(Content.Success(recipe)) }

        onNode(paneTitle(recipe.name)).assertExists()
    }

    @Test
    fun errorIsAnnouncedWhenItAppears() = runComposeUiTest {
        setContent { Screen(Content.Error(DataError.NoConnection)) }

        onNode(paneTitle("Could not reach TheMealDB. Check your connection.")).assertExists()
    }

    @Test
    fun loadingIndicatorIsDescribed() = runComposeUiTest {
        setContent { Screen(Content.Loading) }

        onNodeWithContentDescription("Loading").assertExists()
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

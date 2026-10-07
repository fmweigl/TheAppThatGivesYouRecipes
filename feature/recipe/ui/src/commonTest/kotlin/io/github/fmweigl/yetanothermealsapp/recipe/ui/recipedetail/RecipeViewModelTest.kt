package io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import io.github.fmweigl.yetanothermealsapp.recipe.ui.FakeFavoritesRepository
import io.github.fmweigl.yetanothermealsapp.recipe.ui.recipedetail.RecipeUiState.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Answers every [getRecipe] with [result]. */
    private class FakeRepository : RecipeRepository {
        var result: Result<Recipe, DataError> = Result.Success(RECIPE)
        val requestedIds = mutableListOf<String>()

        override suspend fun getRandomRecipe(): Result<Recipe, DataError> = error("Not used by the recipe screen")

        override suspend fun getRecipe(id: String): Result<Recipe, DataError> {
            requestedIds += id
            return result
        }
    }

    private val repository = FakeRepository()

    private val favorites = FakeFavoritesRepository()

    private fun TestScope.createViewModel() =
        RecipeViewModel(RECIPE.id, repository, favorites).also { advanceUntilIdle() }

    @Test
    fun loadsTheRecipeWithItsId() = runTest(dispatcher) {
        val viewModel = RecipeViewModel(RECIPE.id, repository, favorites)
        assertEquals(RecipeUiState(Content.Loading), viewModel.uiState.value)

        advanceUntilIdle()
        assertEquals(RecipeUiState(Content.Success(RECIPE)), viewModel.uiState.value)
        assertEquals(listOf(RECIPE.id), repository.requestedIds)
    }

    @Test
    fun showsErrorAndRecoversOnRetry() = runTest(dispatcher) {
        repository.result = Result.Failure(DataError.NoConnection)
        val viewModel = createViewModel()
        assertEquals(RecipeUiState(Content.Error(DataError.NoConnection)), viewModel.uiState.value)

        repository.result = Result.Success(RECIPE)
        viewModel.retry()
        advanceUntilIdle()
        assertEquals(RecipeUiState(Content.Success(RECIPE)), viewModel.uiState.value)
    }

    @Test
    fun showsWhetherTheRecipeIsAFavorite() = runTest(dispatcher) {
        favorites.favorites.value = listOf(RECIPE)
        val viewModel = createViewModel()
        assertTrue(viewModel.uiState.value.isFavorite)

        favorites.favorites.value = emptyList()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun removingTheFavoriteKeepsTheRecipeOnScreenAndItCanBeSavedAgain() = runTest(dispatcher) {
        favorites.favorites.value = listOf(RECIPE)
        val viewModel = createViewModel()

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(RecipeUiState(Content.Success(RECIPE), isFavorite = false), viewModel.uiState.value)
        assertEquals(emptyList(), favorites.favorites.value)

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(RecipeUiState(Content.Success(RECIPE), isFavorite = true), viewModel.uiState.value)
        assertEquals(listOf(RECIPE), favorites.favorites.value)
    }

    @Test
    fun toggleFavoriteDoesNothingWithoutARecipe() = runTest(dispatcher) {
        repository.result = Result.Failure(DataError.NotFound)
        val viewModel = createViewModel()

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(emptyList(), favorites.favorites.value)
    }

    private companion object {
        val RECIPE = Recipe(id = "52923", name = "Canadian Butter Tarts")
    }
}

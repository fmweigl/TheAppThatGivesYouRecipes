package io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe.RandomRecipeUiState.Content
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

@OptIn(ExperimentalCoroutinesApi::class)
class RandomRecipeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Returns recipe 1, 2, 3, ... or [error] while it is set. */
    private class FakeRepository : RecipeRepository {
        var error: DataError? = null
        var loadedCount = 0

        override suspend fun getRandomRecipe(): Result<Recipe, DataError> {
            error?.let { return Result.Failure(it) }
            loadedCount++
            return Result.Success(recipe(loadedCount))
        }

        override suspend fun getRecipe(id: String): Result<Recipe, DataError> = error("Not used by the random screen")
    }

    private val repository = FakeRepository()

    private fun TestScope.createViewModel() = RandomRecipeViewModel(repository).also { advanceUntilIdle() }

    private fun TestScope.showNext(viewModel: RandomRecipeViewModel) {
        viewModel.showNext()
        advanceUntilIdle()
    }

    private fun showing(n: Int, canShowPrevious: Boolean) =
        RandomRecipeUiState(Content.Success(recipe(n)), canShowPrevious)

    @Test
    fun loadsRecipeOnCreation() = runTest(dispatcher) {
        val viewModel = RandomRecipeViewModel(repository)

        assertEquals(RandomRecipeUiState(Content.Loading, canShowPrevious = false), viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(showing(1, canShowPrevious = false), viewModel.uiState.value)
    }

    @Test
    fun nextLoadsNewRecipeAndEnablesPrevious() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.showNext()
        assertEquals(RandomRecipeUiState(Content.Loading, canShowPrevious = true), viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(showing(2, canShowPrevious = true), viewModel.uiState.value)
    }

    @Test
    fun stepsBackAndForwardThroughHistoryWithoutReloading() = runTest(dispatcher) {
        val viewModel = createViewModel()
        showNext(viewModel)
        showNext(viewModel)

        viewModel.showPrevious()
        assertEquals(showing(2, canShowPrevious = true), viewModel.uiState.value)
        viewModel.showPrevious()
        assertEquals(showing(1, canShowPrevious = false), viewModel.uiState.value)

        viewModel.showNext()
        assertEquals(showing(2, canShowPrevious = true), viewModel.uiState.value)
        viewModel.showNext()
        assertEquals(showing(3, canShowPrevious = true), viewModel.uiState.value)
        assertEquals(3, repository.loadedCount)

        showNext(viewModel)
        assertEquals(showing(4, canShowPrevious = true), viewModel.uiState.value)
    }

    @Test
    fun previousDoesNothingAtStartOfHistory() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.showPrevious()
        assertEquals(showing(1, canShowPrevious = false), viewModel.uiState.value)
    }

    @Test
    fun previousDuringLoadCancelsItAndShowsLastRecipe() = runTest(dispatcher) {
        val viewModel = createViewModel()

        viewModel.showNext()
        viewModel.showPrevious()
        advanceUntilIdle()
        assertEquals(showing(1, canShowPrevious = false), viewModel.uiState.value)
        assertEquals(1, repository.loadedCount)
    }

    @Test
    fun showsErrorAndRecoversOnRetry() = runTest(dispatcher) {
        repository.error = DataError.NoConnection
        val viewModel = createViewModel()
        assertEquals(
            RandomRecipeUiState(Content.Error(DataError.NoConnection), canShowPrevious = false),
            viewModel.uiState.value,
        )

        repository.error = null
        showNext(viewModel)
        assertEquals(showing(1, canShowPrevious = false), viewModel.uiState.value)
    }

    @Test
    fun previousAfterErrorShowsLastRecipe() = runTest(dispatcher) {
        val viewModel = createViewModel()
        repository.error = DataError.Server
        showNext(viewModel)
        assertEquals(
            RandomRecipeUiState(Content.Error(DataError.Server), canShowPrevious = true),
            viewModel.uiState.value,
        )

        viewModel.showPrevious()
        assertEquals(showing(1, canShowPrevious = false), viewModel.uiState.value)
    }

    @Test
    fun dropsOldestRecipesBeyondHistoryLimit() = runTest(dispatcher) {
        val viewModel = createViewModel()
        repeat(MAX_HISTORY_SIZE) { showNext(viewModel) }
        assertEquals(showing(MAX_HISTORY_SIZE + 1, canShowPrevious = true), viewModel.uiState.value)

        repeat(MAX_HISTORY_SIZE - 1) { viewModel.showPrevious() }
        assertEquals(showing(2, canShowPrevious = false), viewModel.uiState.value)
    }

    private companion object {
        fun recipe(n: Int) = Recipe(id = "$n", name = "Recipe $n")
    }
}

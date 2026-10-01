package com.example.yetanothermealsapp.randomrecipe.ui

import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import com.example.yetanothermealsapp.randomrecipe.domain.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

    private class FakeRepository(var result: () -> Recipe) : RandomRecipeRepository {
        override suspend fun getRandomRecipe(): Recipe = result()
    }

    @Test
    fun loadsRecipeOnCreation() = runTest(dispatcher) {
        val recipe = Recipe(id = "1", name = "Soup")
        val viewModel = RandomRecipeViewModel(FakeRepository { recipe })

        assertEquals(RandomRecipeUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(RandomRecipeUiState.Success(recipe), viewModel.uiState.value)
    }

    @Test
    fun showsErrorAndRecoversOnRetry() = runTest(dispatcher) {
        val repository = FakeRepository { throw IllegalStateException("offline") }
        val viewModel = RandomRecipeViewModel(repository)
        advanceUntilIdle()
        assertEquals(RandomRecipeUiState.Error, viewModel.uiState.value)

        val recipe = Recipe(id = "2", name = "Pie")
        repository.result = { recipe }
        viewModel.loadRandomRecipe()
        advanceUntilIdle()
        assertEquals(RandomRecipeUiState.Success(recipe), viewModel.uiState.value)
    }
}

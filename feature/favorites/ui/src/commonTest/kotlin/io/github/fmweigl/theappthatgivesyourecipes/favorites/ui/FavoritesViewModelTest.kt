package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.favorites.ui.FavoritesUiState.Content
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val repository = FakeFavoritesRepository()

    private fun TestScope.createViewModel() = FavoritesViewModel(repository).also { advanceUntilIdle() }

    private fun FavoritesViewModel.shownIds() =
        (uiState.value.content as Content.Favorites).teasers.map { it.id }

    @Test
    fun showsTheFavoritesAsTeasers() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS, SOUP)
        val viewModel = FavoritesViewModel(repository)
        assertEquals(Content.Loading, viewModel.uiState.value.content)

        advanceUntilIdle()
        assertEquals(
            Content.Favorites(
                listOf(
                    RecipeTeaser(
                        id = "52923",
                        name = "Canadian Butter Tarts",
                        subtitle = "Dessert · Canadian",
                        imageUrl = "https://example.com/tarts.jpg",
                    ),
                    RecipeTeaser("1", "Soup", subtitle = null, imageUrl = null),
                ),
            ),
            viewModel.uiState.value.content,
        )
    }

    @Test
    fun showsEmptyWithoutFavorites() = runTest(dispatcher) {
        assertEquals(Content.Empty, createViewModel().uiState.value.content)
    }

    @Test
    fun showsErrorWhenTheFavoritesCantBeRead() = runTest(dispatcher) {
        repository.readFails = true

        assertEquals(Content.Error, createViewModel().uiState.value.content)
    }

    @Test
    fun removeDeletesTheFavoriteAndOffersUndo() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS, SOUP)
        val viewModel = createViewModel()

        viewModel.remove(SOUP.id)
        advanceUntilIdle()
        assertEquals(listOf(TARTS), repository.favorites)
        assertEquals(listOf(TARTS.id), viewModel.shownIds())
        assertEquals("Soup", viewModel.uiState.value.removed?.name)
    }

    @Test
    fun removingShowsTheHeartEmptyUntilTheCardIsGone() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS, SOUP)
        val viewModel = createViewModel()

        viewModel.remove(SOUP.id)
        runCurrent()
        assertEquals(setOf(SOUP.id), viewModel.uiState.value.removing)

        advanceTimeBy(REMOVE_DELAY_MILLIS - 1)
        assertEquals(listOf(TARTS, SOUP), repository.favorites, "the heart's animation plays first")

        advanceUntilIdle()
        assertEquals(listOf(TARTS.id), viewModel.shownIds())
        assertEquals(emptySet(), viewModel.uiState.value.removing)
    }

    @Test
    fun undoRestoresTheRecipeAtItsOldPosition() = runTest(dispatcher) {
        val stew = Recipe(id = "2", name = "Stew")
        repository.favorites = listOf(TARTS, SOUP, stew)
        val viewModel = createViewModel()
        viewModel.remove(SOUP.id)
        advanceUntilIdle()

        viewModel.removalMessageClosed(undo = true)
        advanceUntilIdle()
        assertEquals(listOf(TARTS, SOUP, stew), repository.favorites)
        assertEquals(listOf(TARTS.id, SOUP.id, stew.id), viewModel.shownIds())
        assertNull(viewModel.uiState.value.removed)
    }

    @Test
    fun failedUndoIsReportedUntilItsMessageCloses() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS, SOUP)
        val viewModel = createViewModel()
        viewModel.remove(SOUP.id)
        advanceUntilIdle()
        repository.restoreError = DataError.Storage

        viewModel.removalMessageClosed(undo = true)
        advanceUntilIdle()
        assertEquals(listOf(TARTS), repository.favorites)
        assertEquals("Soup", viewModel.uiState.value.restoreFailed?.name)

        viewModel.restoreFailureMessageClosed()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.restoreFailed)
    }

    @Test
    fun aNewRemovalClearsAStaleRestoreFailure() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS, SOUP)
        val viewModel = createViewModel()
        viewModel.remove(SOUP.id)
        advanceUntilIdle()
        repository.restoreError = DataError.Storage
        viewModel.removalMessageClosed(undo = true)
        advanceUntilIdle()

        viewModel.remove(TARTS.id)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.restoreFailed)
        assertEquals("Canadian Butter Tarts", viewModel.uiState.value.removed?.name)
    }

    @Test
    fun removingTheSameRecipeTwiceKeepsTheUndo() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS, SOUP)
        val viewModel = createViewModel()

        viewModel.remove(SOUP.id)
        viewModel.remove(SOUP.id)
        advanceUntilIdle()
        assertEquals("Soup", viewModel.uiState.value.removed?.name)

        viewModel.removalMessageClosed(undo = true)
        advanceUntilIdle()
        assertEquals(listOf(TARTS, SOUP), repository.favorites)
    }

    @Test
    fun closingTheMessageWithoutUndoKeepsTheFavoriteDeleted() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS)
        val viewModel = createViewModel()
        viewModel.remove(TARTS.id)
        advanceUntilIdle()

        viewModel.removalMessageClosed(undo = false)
        advanceUntilIdle()
        assertEquals(emptyList(), repository.favorites)
        assertEquals(Content.Empty, viewModel.uiState.value.content)
        assertNull(viewModel.uiState.value.removed)
    }

    @Test
    fun failedRemoveKeepsTheFavoriteAndOffersNoUndo() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS)
        val viewModel = createViewModel()
        repository.writeError = DataError.Storage

        viewModel.remove(TARTS.id)
        advanceUntilIdle()
        assertEquals(listOf(TARTS.id), viewModel.shownIds())
        assertNull(viewModel.uiState.value.removed)
    }

    @Test
    fun failedRemoveFillsTheHeartAgainAndIsReportedUntilItsMessageCloses() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS)
        val viewModel = createViewModel()
        repository.writeError = DataError.Storage

        viewModel.remove(TARTS.id)
        advanceUntilIdle()
        assertEquals(emptySet(), viewModel.uiState.value.removing)
        assertEquals("Canadian Butter Tarts", viewModel.uiState.value.removeFailed?.name)

        viewModel.removeFailureMessageClosed()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.removeFailed)
    }

    @Test
    fun retryingAFailedRemoveClearsTheOldFailure() = runTest(dispatcher) {
        repository.favorites = listOf(TARTS)
        val viewModel = createViewModel()
        repository.writeError = DataError.Storage
        viewModel.remove(TARTS.id)
        advanceUntilIdle()
        repository.writeError = null

        viewModel.remove(TARTS.id)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.removeFailed)
        assertEquals("Canadian Butter Tarts", viewModel.uiState.value.removed?.name)
    }

    private companion object {
        val TARTS = Recipe(
            id = "52923",
            name = "Canadian Butter Tarts",
            category = "Dessert",
            area = "Canadian",
            imageUrl = "https://example.com/tarts.jpg",
        )
        val SOUP = Recipe(id = "1", name = "Soup")
    }
}

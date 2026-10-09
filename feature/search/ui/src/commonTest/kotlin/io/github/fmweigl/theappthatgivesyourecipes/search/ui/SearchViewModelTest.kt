package io.github.fmweigl.theappthatgivesyourecipes.search.ui

import androidx.lifecycle.SavedStateHandle
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.repository.RecipeRepository
import io.github.fmweigl.theappthatgivesyourecipes.search.ui.SearchUiState.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /**
     * Answers a search after [delayMillis] with the recipes in [recipes] whose name contains the
     * query, or with [error] while it is set. Remembers every query it was asked.
     */
    private class FakeRepository : RecipeRepository {
        var recipes: List<Recipe> = emptyList()
        var error: DataError? = null
        var delayMillis = 0L
        val queries = mutableListOf<String>()

        override suspend fun searchRecipes(query: String): Result<List<Recipe>, DataError> {
            queries += query
            delay(delayMillis)
            error?.let { return Result.Failure(it) }
            return Result.Success(recipes.filter { it.name.lowercase().contains(query) })
        }

        override suspend fun getRandomRecipe(): Result<Recipe, DataError> = error("Not used by the search")

        override suspend fun getRecipe(id: String): Result<Recipe, DataError> = error("Not used by the search")
    }

    private val repository = FakeRepository()

    private fun createViewModel(handle: SavedStateHandle = SavedStateHandle()) =
        SearchViewModel(handle, repository)

    private fun SearchViewModel.shownNames() =
        (uiState.value.content as Content.Results).results.map { it.name }

    private fun TestScope.type(viewModel: SearchViewModel, text: String) {
        viewModel.onQueryChange(text)
        runCurrent()
    }

    @Test
    fun startsIdleAndDoesNotSearch() = runTest(dispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(Content.Idle, viewModel.uiState.value.content)
        assertEquals(emptyList(), repository.queries)
    }

    @Test
    fun searchesOnlyAfterTheDebounce() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY)
        val viewModel = createViewModel()

        type(viewModel, "chick")
        advanceTimeBy(DEBOUNCE_MILLIS - 1)
        assertEquals(emptyList(), repository.queries)
        assertEquals(Content.Idle, viewModel.uiState.value.content)

        advanceTimeBy(1)
        runCurrent()
        assertEquals(listOf("chick"), repository.queries)
        assertEquals(listOf("Chicken curry"), viewModel.shownNames())
    }

    @Test
    fun everyKeystrokeRestartsTheDebounce() = runTest(dispatcher) {
        val viewModel = createViewModel()

        type(viewModel, "ch")
        advanceTimeBy(DEBOUNCE_MILLIS - 1)
        type(viewModel, "chi")
        advanceTimeBy(DEBOUNCE_MILLIS - 1)
        assertEquals(emptyList(), repository.queries)

        advanceUntilIdle()
        assertEquals(listOf("chi"), repository.queries)
    }

    @Test
    fun doesNotSearchForFewerThanTwoCharacters() = runTest(dispatcher) {
        val viewModel = createViewModel()

        type(viewModel, "c")
        advanceUntilIdle()
        assertEquals(emptyList(), repository.queries)
        assertEquals(Content.Idle, viewModel.uiState.value.content)

        type(viewModel, " c ")
        advanceUntilIdle()
        assertEquals(emptyList(), repository.queries)

        type(viewModel, "ch")
        advanceUntilIdle()
        assertEquals(listOf("ch"), repository.queries)
    }

    @Test
    fun shrinkingTheQueryBelowTheMinimumClearsTheResults() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY)
        val viewModel = createViewModel()
        type(viewModel, "chick")
        advanceUntilIdle()

        type(viewModel, "c")
        advanceUntilIdle()

        assertEquals(Content.Idle, viewModel.uiState.value.content)
        assertEquals(false, viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun normalizesTheQuery() = runTest(dispatcher) {
        val viewModel = createViewModel()

        type(viewModel, "  Pad   THAI \n")
        advanceUntilIdle()

        assertEquals(listOf("pad thai"), repository.queries)
        // The field keeps what was typed.
        assertEquals("  Pad   THAI \n", viewModel.uiState.value.query)
    }

    @Test
    fun equalNormalizedQueriesSearchOnce() = runTest(dispatcher) {
        val viewModel = createViewModel()
        type(viewModel, "chick")
        advanceUntilIdle()

        type(viewModel, "Chick ")
        advanceUntilIdle()

        assertEquals(listOf("chick"), repository.queries)
    }

    @Test
    fun showsNoMatchWithTheQuery() = runTest(dispatcher) {
        val viewModel = createViewModel()

        type(viewModel, "Xyzzy")
        advanceUntilIdle()

        assertEquals(Content.NoMatch("xyzzy"), viewModel.uiState.value.content)
    }

    @Test
    fun mapsRecipesToRows() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY, Recipe(id = "2", name = "Chicken soup"))
        val viewModel = createViewModel()

        type(viewModel, "chicken")
        advanceUntilIdle()

        assertEquals(
            Content.Results(
                listOf(
                    SearchResult(
                        id = "1",
                        name = "Chicken curry",
                        subtitle = "Chicken · Indian",
                        thumbnailUrl = "https://example.com/curry.jpg/small",
                    ),
                    SearchResult(id = "2", name = "Chicken soup", subtitle = null, thumbnailUrl = null),
                ),
            ),
            viewModel.uiState.value.content,
        )
    }

    @Test
    fun marksResultsAsCappedAtTheApisLimit() = runTest(dispatcher) {
        repository.recipes = (1..MAX_RESULTS).map { Recipe(id = "$it", name = "Soup $it") }
        val viewModel = createViewModel()

        type(viewModel, "soup")
        advanceUntilIdle()
        assertEquals(true, (viewModel.uiState.value.content as Content.Results).isCapped)

        repository.recipes = repository.recipes.drop(1)
        viewModel.search()
        advanceUntilIdle()
        assertEquals(false, (viewModel.uiState.value.content as Content.Results).isCapped)
    }

    @Test
    fun showsTheSkeletonForTheFirstSearchAndKeepsResultsWhileRefreshing() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY, Recipe(id = "2", name = "Chicken soup"))
        repository.delayMillis = 100
        val viewModel = createViewModel()

        type(viewModel, "chick")
        advanceTimeBy(DEBOUNCE_MILLIS + 1)
        assertEquals(Content.Loading, viewModel.uiState.value.content)

        advanceUntilIdle()
        assertEquals(2, viewModel.shownNames().size)

        type(viewModel, "chicken s")
        advanceTimeBy(DEBOUNCE_MILLIS + 1)
        assertEquals(2, viewModel.shownNames().size)
        assertEquals(true, viewModel.uiState.value.isRefreshing)

        advanceUntilIdle()
        assertEquals(listOf("Chicken soup"), viewModel.shownNames())
        assertEquals(false, viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun anOutdatedRequestNeverOverwritesNewerResults() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY, Recipe(id = "2", name = "Chicory salad"))
        repository.delayMillis = 1_000
        val viewModel = createViewModel()

        type(viewModel, "chi")
        advanceTimeBy(DEBOUNCE_MILLIS + 1) // "chi" is in flight
        type(viewModel, "chick")
        advanceUntilIdle()

        assertEquals(listOf("chi", "chick"), repository.queries)
        assertEquals(listOf("Chicken curry"), viewModel.shownNames())
    }

    @Test
    fun anOutdatedRequestThatFinishesLastIsStillIgnored() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY, Recipe(id = "2", name = "Chicory salad"))
        val viewModel = createViewModel()

        repository.delayMillis = 2_000
        type(viewModel, "chi")
        advanceTimeBy(DEBOUNCE_MILLIS + 1)
        repository.delayMillis = 10
        type(viewModel, "chick")
        advanceTimeBy(DEBOUNCE_MILLIS + 100)
        assertEquals(listOf("Chicken curry"), viewModel.shownNames())

        advanceTimeBy(5_000)
        assertEquals(listOf("Chicken curry"), viewModel.shownNames())
    }

    @Test
    fun theKeyboardsSearchActionSkipsTheDebounce() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY)
        val viewModel = createViewModel()

        type(viewModel, "chick")
        viewModel.search()
        runCurrent()

        assertEquals(listOf("chick"), repository.queries)
        assertEquals(listOf("Chicken curry"), viewModel.shownNames())

        // The debounced search of the same query doesn't repeat it.
        advanceUntilIdle()
        assertEquals(listOf("chick"), repository.queries)
    }

    @Test
    fun theKeyboardsSearchActionWithAShortQueryDoesNotSearch() = runTest(dispatcher) {
        val viewModel = createViewModel()

        type(viewModel, "c")
        viewModel.search()
        advanceUntilIdle()

        assertEquals(emptyList(), repository.queries)
    }

    @Test
    fun showsTheErrorAndRetrySearchesAgain() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY)
        repository.error = DataError.NoConnection
        val viewModel = createViewModel()

        type(viewModel, "chick")
        advanceUntilIdle()
        assertEquals(Content.Error(DataError.NoConnection), viewModel.uiState.value.content)

        repository.error = null
        repository.delayMillis = 100
        viewModel.retry()
        runCurrent()
        assertEquals(Content.Loading, viewModel.uiState.value.content)
        advanceUntilIdle()

        assertEquals(listOf("chick", "chick"), repository.queries)
        assertEquals(listOf("Chicken curry"), viewModel.shownNames())
    }

    @Test
    fun theQuerySurvivesProcessDeath() = runTest(dispatcher) {
        repository.recipes = listOf(CHICKEN_CURRY)
        val handle = SavedStateHandle()
        val first = createViewModel(handle)
        type(first, "Chick")
        advanceUntilIdle()

        val restored = createViewModel(handle)
        assertEquals("Chick", restored.uiState.value.query)
        advanceUntilIdle()

        assertIs<Content.Results>(restored.uiState.value.content)
    }

    private companion object {
        val CHICKEN_CURRY = Recipe(
            id = "1",
            name = "Chicken curry",
            category = "Chicken",
            area = "Indian",
            imageUrl = "https://example.com/curry.jpg",
        )
    }
}

package io.github.fmweigl.yetanothermealsapp.recipe.data.repository

import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.inMemoryRecipeDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.testRecipe
import io.github.fmweigl.yetanothermealsapp.recipe.domain.model.Recipe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

class FavoritesRepositoryImplTest {

    private val database = inMemoryRecipeDatabase()

    /** Each favorite is saved one millisecond after the previous one. */
    private val clock = object : Clock {
        private var millis = 1_000L
        override fun now() = Instant.fromEpochMilliseconds(millis++)
    }

    private val repository = FavoritesRepositoryImpl(database.favoriteRecipeDao(), clock)

    @AfterTest
    fun closeDatabase() = database.close()

    private suspend fun favorites(): List<Recipe> =
        assertIs<Result.Success<List<Recipe>>>(repository.observeFavorites().first()).data

    /** Collects [flow] in the background, so a test can wait for each value it emits. */
    private fun <T> TestScope.collect(flow: Flow<T>): Channel<T> {
        val values = Channel<T>(Channel.UNLIMITED)
        backgroundScope.launch { flow.collect { values.send(it) } }
        return values
    }

    @Test
    fun storesTheWholeRecipe() = runTest {
        val recipe = testRecipe("52923")

        assertEquals(Result.Success(Unit), repository.addFavorite(recipe))

        assertEquals(listOf(recipe), favorites())
    }

    @Test
    fun listsTheMostRecentlySavedFirst() = runTest {
        repository.addFavorite(testRecipe("1"))
        repository.addFavorite(testRecipe("2"))
        repository.addFavorite(testRecipe("3"))

        assertEquals(listOf("3", "2", "1"), favorites().map { it.id })
    }

    @Test
    fun addingAFavoriteAgainUpdatesIt() = runTest {
        repository.addFavorite(testRecipe("1", name = "Old name"))
        repository.addFavorite(testRecipe("2"))
        repository.addFavorite(testRecipe("1", name = "New name"))

        assertEquals(listOf("New name", "Recipe 2"), favorites().map { it.name })
    }

    @Test
    fun removesFavorites() = runTest {
        repository.addFavorite(testRecipe("1"))
        repository.addFavorite(testRecipe("2"))

        assertEquals(Result.Success(Unit), repository.removeFavorite("1"))

        assertEquals(listOf("2"), favorites().map { it.id })
    }

    @Test
    fun removingARecipeThatIsNoFavoriteDoesNothing() = runTest {
        repository.addFavorite(testRecipe("1"))

        assertEquals(Result.Success(Unit), repository.removeFavorite("2"))

        assertEquals(listOf("1"), favorites().map { it.id })
    }

    @Test
    fun observeFavoritesEmitsWhenTheFavoritesChange() = runTest {
        val favorites = collect(repository.observeFavorites())
        assertEquals(Result.Success(emptyList()), favorites.receive())

        repository.addFavorite(testRecipe("1"))
        assertEquals(Result.Success(listOf(testRecipe("1"))), favorites.receive())

        repository.removeFavorite("1")
        assertEquals(Result.Success(emptyList()), favorites.receive())
    }

    @Test
    fun observeIsFavoriteEmitsWhenTheRecipeIsAddedAndRemoved() = runTest {
        val isFavorite = collect(repository.observeIsFavorite("1"))
        assertFalse(isFavorite.receive())

        repository.addFavorite(testRecipe("1"))
        assertTrue(isFavorite.receive())

        repository.removeFavorite("1")
        assertFalse(isFavorite.receive())
    }

    @Test
    fun observeIsFavoriteIgnoresOtherRecipes() = runTest {
        repository.addFavorite(testRecipe("2"))

        assertFalse(repository.observeIsFavorite("1").first())
        assertTrue(repository.observeIsFavorite("2").first())
    }
}

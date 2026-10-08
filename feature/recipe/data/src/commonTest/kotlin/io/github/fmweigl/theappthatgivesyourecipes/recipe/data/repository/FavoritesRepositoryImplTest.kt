package io.github.fmweigl.theappthatgivesyourecipes.recipe.data.repository

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.inMemoryRecipeDatabase
import io.github.fmweigl.theappthatgivesyourecipes.recipe.data.database.testRecipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.Recipe
import io.github.fmweigl.theappthatgivesyourecipes.recipe.domain.model.RemovedFavorite
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

        val removed = assertIs<Result.Success<RemovedFavorite>>(repository.removeFavorite("1")).data

        assertEquals(testRecipe("1"), removed.recipe)
        assertEquals(listOf("2"), favorites().map { it.id })
    }

    @Test
    fun removingARecipeThatIsNoFavoriteFailsAndChangesNothing() = runTest {
        repository.addFavorite(testRecipe("1"))

        assertEquals(Result.Failure(DataError.NotFound), repository.removeFavorite("2"))

        assertEquals(listOf("1"), favorites().map { it.id })
    }

    @Test
    fun aRestoredFavoriteReturnsToItsOldPosition() = runTest {
        repository.addFavorite(testRecipe("1"))
        repository.addFavorite(testRecipe("2"))
        repository.addFavorite(testRecipe("3"))
        val removed = assertIs<Result.Success<RemovedFavorite>>(repository.removeFavorite("2")).data
        repository.addFavorite(testRecipe("4"))

        assertEquals(Result.Success(Unit), repository.restoreFavorite(removed))

        assertEquals(listOf("4", "3", "2", "1"), favorites().map { it.id })
        assertEquals(testRecipe("2"), favorites()[2])
    }

    @Test
    fun restoringAFavoriteThatWasAddedAgainKeepsTheNewerOne() = runTest {
        repository.addFavorite(testRecipe("1"))
        repository.addFavorite(testRecipe("2"))
        val removed = assertIs<Result.Success<RemovedFavorite>>(repository.removeFavorite("1")).data
        repository.addFavorite(testRecipe("1", name = "Newer"))

        assertEquals(Result.Success(Unit), repository.restoreFavorite(removed))

        assertEquals(listOf("Newer", "Recipe 2"), favorites().map { it.name })
    }

    @Test
    fun removingAndRestoringMapStorageFailures() = runTest {
        repository.addFavorite(testRecipe("1"))
        val removed = assertIs<Result.Success<RemovedFavorite>>(repository.removeFavorite("1")).data
        // The table is gone, so every statement fails with an SQLiteException.
        database.useConnection(isReadOnly = false) {
            it.usePrepared("DROP TABLE favorite_recipe") { statement -> statement.step() }
        }

        assertEquals(Result.Failure(DataError.Storage), repository.removeFavorite("1"))
        assertEquals(Result.Failure(DataError.Storage), repository.restoreFavorite(removed))
    }

    @Test
    fun addingARemovedFavoriteAgainPutsItOnTop() = runTest {
        repository.addFavorite(testRecipe("1"))
        repository.addFavorite(testRecipe("2"))
        repository.removeFavorite("1")

        repository.addFavorite(testRecipe("1"))

        assertEquals(listOf("1", "2"), favorites().map { it.id })
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

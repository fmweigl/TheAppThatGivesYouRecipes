package io.github.fmweigl.yetanothermealsapp.recipe.data.di

import androidx.room3.RoomDatabase
import io.github.fmweigl.yetanothermealsapp.core.network.di.coreNetworkModule
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.RecipeDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.inMemoryRecipeDatabaseBuilder
import io.github.fmweigl.yetanothermealsapp.recipe.data.repository.FavoritesRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.recipe.data.repository.RecipeRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class RecipeDataModuleTest {

    /** Replaces the platform's database file, so the test doesn't write to the user's app data. */
    private val inMemoryDatabaseModule = module {
        single<RoomDatabase.Builder<RecipeDatabase>> { inMemoryRecipeDatabaseBuilder() }
    }

    @Test
    fun resolvesRepositoriesSharingOneDatabase() {
        val koin = koinApplication { modules(coreNetworkModule, recipeDataModule, inMemoryDatabaseModule) }.koin
        try {
            assertIs<RecipeRepositoryImpl>(koin.get<RecipeRepository>())
            assertIs<FavoritesRepositoryImpl>(koin.get<FavoritesRepository>())
            assertSame(koin.get<RecipeDatabase>(), koin.get<RecipeDatabase>())
        } finally {
            koin.get<RecipeDatabase>().close()
            koin.close()
        }
    }
}

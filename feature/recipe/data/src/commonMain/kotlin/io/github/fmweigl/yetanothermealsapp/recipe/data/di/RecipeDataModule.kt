package io.github.fmweigl.yetanothermealsapp.recipe.data.di

import androidx.room3.RoomDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.RecipeDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.database.buildRecipeDatabase
import io.github.fmweigl.yetanothermealsapp.recipe.data.repository.FavoritesRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.recipe.data.repository.RecipeRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.FavoritesRepository
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Needs the `HttpClient` from `coreNetworkModule`, and on Android the `Context` from `androidContext()`. */
val recipeDataModule = module {
    includes(platformDatabaseModule)
    single { get<RoomDatabase.Builder<RecipeDatabase>>().buildRecipeDatabase() }
    single { get<RecipeDatabase>().favoriteRecipeDao() }
    singleOf(::RecipeRepositoryImpl) { bind<RecipeRepository>() }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
}

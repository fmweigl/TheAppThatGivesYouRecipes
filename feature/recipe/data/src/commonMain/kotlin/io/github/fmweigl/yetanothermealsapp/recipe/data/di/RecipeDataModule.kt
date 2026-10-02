package io.github.fmweigl.yetanothermealsapp.recipe.data.di

import io.github.fmweigl.yetanothermealsapp.recipe.data.RecipeRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.recipe.domain.RecipeRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Needs the `HttpClient` from `coreNetworkModule`. */
val recipeDataModule = module {
    singleOf(::RecipeRepositoryImpl) { bind<RecipeRepository>() }
}

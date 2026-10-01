package io.github.fmweigl.yetanothermealsapp.randomrecipe.data.di

import io.github.fmweigl.yetanothermealsapp.randomrecipe.data.RandomRecipeRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Needs the `HttpClient` from `coreNetworkModule`. */
val randomRecipeDataModule = module {
    singleOf(::RandomRecipeRepositoryImpl) { bind<RandomRecipeRepository>() }
}

package io.github.fmweigl.yetanothermealsapp.randomrecipe.data.di

import io.github.fmweigl.yetanothermealsapp.randomrecipe.data.RandomRecipeRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.randomrecipe.data.createTheMealDbHttpClient
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val randomRecipeDataModule = module {
    single { createTheMealDbHttpClient() }
    singleOf(::RandomRecipeRepositoryImpl) { bind<RandomRecipeRepository>() }
}

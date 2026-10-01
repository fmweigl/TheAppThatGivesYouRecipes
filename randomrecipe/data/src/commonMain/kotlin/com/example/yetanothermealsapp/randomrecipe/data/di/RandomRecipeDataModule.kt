package com.example.yetanothermealsapp.randomrecipe.data.di

import com.example.yetanothermealsapp.randomrecipe.data.RandomRecipeRepositoryImpl
import com.example.yetanothermealsapp.randomrecipe.data.createTheMealDbHttpClient
import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val randomRecipeDataModule = module {
    single { createTheMealDbHttpClient() }
    singleOf(::RandomRecipeRepositoryImpl) { bind<RandomRecipeRepository>() }
}

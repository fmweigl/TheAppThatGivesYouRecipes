package com.example.yetanothermealsapp.randomrecipe.ui.di

import com.example.yetanothermealsapp.randomrecipe.ui.RandomRecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val randomRecipeUiModule = module {
    viewModelOf(::RandomRecipeViewModel)
}

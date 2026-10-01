package io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.di

import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.RandomRecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val randomRecipeUiModule = module {
    viewModelOf(::RandomRecipeViewModel)
}

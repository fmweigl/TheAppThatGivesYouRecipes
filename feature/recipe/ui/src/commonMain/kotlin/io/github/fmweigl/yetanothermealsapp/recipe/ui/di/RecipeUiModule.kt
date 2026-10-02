package io.github.fmweigl.yetanothermealsapp.recipe.ui.di

import io.github.fmweigl.yetanothermealsapp.recipe.ui.randomrecipe.RandomRecipeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val recipeUiModule = module {
    viewModelOf(::RandomRecipeViewModel)
}

package io.github.fmweigl.theappthatgivesyourecipes.search.ui.di

import io.github.fmweigl.theappthatgivesyourecipes.search.ui.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Needs the `RecipeRepository` from `recipeDataModule`. */
val searchUiModule = module {
    viewModelOf(::SearchViewModel)
}

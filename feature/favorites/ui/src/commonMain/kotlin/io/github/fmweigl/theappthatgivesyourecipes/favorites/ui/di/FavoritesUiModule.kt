package io.github.fmweigl.theappthatgivesyourecipes.favorites.ui.di

import io.github.fmweigl.theappthatgivesyourecipes.favorites.ui.FavoritesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Needs the `FavoritesRepository` from `recipeDataModule`. */
val favoritesUiModule = module {
    viewModelOf(::FavoritesViewModel)
}

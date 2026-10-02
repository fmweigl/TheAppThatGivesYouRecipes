package io.github.fmweigl.yetanothermealsapp.favorites.ui.di

import io.github.fmweigl.yetanothermealsapp.favorites.ui.FavoritesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Needs the `FavoritesRepository` from `recipeDataModule`. */
val favoritesUiModule = module {
    viewModelOf(::FavoritesViewModel)
}

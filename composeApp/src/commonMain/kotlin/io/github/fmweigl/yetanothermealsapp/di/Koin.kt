package io.github.fmweigl.yetanothermealsapp.di

import io.github.fmweigl.yetanothermealsapp.core.network.di.coreNetworkModule
import io.github.fmweigl.yetanothermealsapp.favorites.ui.di.favoritesUiModule
import io.github.fmweigl.yetanothermealsapp.recipe.data.di.recipeDataModule
import io.github.fmweigl.yetanothermealsapp.recipe.ui.di.recipeUiModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Starts Koin for the whole app. Call it once from each platform's entry point,
 * before any UI is shown. [config] adds platform setup such as `androidContext`; it runs after
 * the app's modules are loaded, so modules it adds override their definitions (the Android debug
 * build's screenshot mode replaces the recipe repository this way).
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        modules(coreNetworkModule, recipeDataModule, recipeUiModule, favoritesUiModule)
        config?.invoke(this)
    }
}

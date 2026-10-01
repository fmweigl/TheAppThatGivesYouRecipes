package com.example.yetanothermealsapp.di

import com.example.yetanothermealsapp.randomrecipe.data.di.randomRecipeDataModule
import com.example.yetanothermealsapp.randomrecipe.ui.di.randomRecipeUiModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Starts Koin for the whole app. Call it once from each platform's entry point,
 * before any UI is shown. [config] adds platform setup such as `androidContext`.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(randomRecipeDataModule, randomRecipeUiModule)
    }
}

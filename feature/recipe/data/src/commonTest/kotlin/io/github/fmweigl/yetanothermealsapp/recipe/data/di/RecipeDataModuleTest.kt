package io.github.fmweigl.yetanothermealsapp.recipe.data.di

import io.github.fmweigl.yetanothermealsapp.core.network.di.coreNetworkModule
import io.github.fmweigl.yetanothermealsapp.recipe.data.repository.RecipeRepositoryImpl
import io.github.fmweigl.yetanothermealsapp.recipe.domain.repository.RecipeRepository
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertIs

class RecipeDataModuleTest {

    @Test
    fun resolvesRepository() {
        val koin = koinApplication { modules(coreNetworkModule, recipeDataModule) }.koin
        try {
            assertIs<RecipeRepositoryImpl>(koin.get<RecipeRepository>())
        } finally {
            koin.close()
        }
    }
}

package io.github.fmweigl.yetanothermealsapp.recipe.data

import io.github.fmweigl.yetanothermealsapp.core.network.di.coreNetworkModule
import io.github.fmweigl.yetanothermealsapp.recipe.data.di.recipeDataModule
import io.github.fmweigl.yetanothermealsapp.recipe.domain.RecipeRepository
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

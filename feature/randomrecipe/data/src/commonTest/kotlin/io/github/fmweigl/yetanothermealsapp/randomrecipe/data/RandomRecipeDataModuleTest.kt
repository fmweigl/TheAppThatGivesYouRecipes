package io.github.fmweigl.yetanothermealsapp.randomrecipe.data

import io.github.fmweigl.yetanothermealsapp.core.network.di.coreNetworkModule
import io.github.fmweigl.yetanothermealsapp.randomrecipe.data.di.randomRecipeDataModule
import io.github.fmweigl.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertIs

class RandomRecipeDataModuleTest {

    @Test
    fun resolvesRepository() {
        val koin = koinApplication { modules(coreNetworkModule, randomRecipeDataModule) }.koin
        try {
            assertIs<RandomRecipeRepositoryImpl>(koin.get<RandomRecipeRepository>())
        } finally {
            koin.close()
        }
    }
}

package com.example.yetanothermealsapp.randomrecipe.data

import com.example.yetanothermealsapp.randomrecipe.data.di.randomRecipeDataModule
import com.example.yetanothermealsapp.randomrecipe.domain.RandomRecipeRepository
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertIs

class RandomRecipeDataModuleTest {

    @Test
    fun resolvesRepository() {
        val koin = koinApplication { modules(randomRecipeDataModule) }.koin
        try {
            assertIs<RandomRecipeRepositoryImpl>(koin.get<RandomRecipeRepository>())
        } finally {
            koin.close()
        }
    }
}

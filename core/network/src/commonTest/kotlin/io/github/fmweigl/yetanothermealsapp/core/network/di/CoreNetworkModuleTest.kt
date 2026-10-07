package io.github.fmweigl.yetanothermealsapp.core.network.di

import io.ktor.client.HttpClient
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertSame

class CoreNetworkModuleTest {

    @Test
    fun providesOneSharedClient() {
        val koin = koinApplication { modules(coreNetworkModule) }.koin
        try {
            assertSame(koin.get<HttpClient>(), koin.get<HttpClient>())
        } finally {
            koin.close()
        }
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.core.network.di

import io.github.fmweigl.theappthatgivesyourecipes.core.network.createTheMealDbHttpClient
import org.koin.dsl.module

/** Provides the app's single TheMealDB `HttpClient`. Features inject it rather than creating their own. */
val coreNetworkModule = module {
    single { createTheMealDbHttpClient() }
}

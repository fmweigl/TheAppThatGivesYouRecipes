package io.github.fmweigl.yetanothermealsapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val THE_MEAL_DB_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

/** Creates the app's client for TheMealDB. The engine comes from the platform (OkHttp on JVM, Darwin on iOS). */
internal fun createTheMealDbHttpClient(): HttpClient = HttpClient { theMealDbConfig() }

/**
 * Creates a client for TheMealDB on [engine], configured like the app's client. Meant for tests with
 * a `MockEngine`; app code injects the shared [HttpClient] from `coreNetworkModule` instead.
 */
fun createTheMealDbHttpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine) { theMealDbConfig() }

/** Relative request URLs resolve against TheMealDB, and non-2xx responses throw. */
private fun HttpClientConfig<*>.theMealDbConfig() {
    expectSuccess = true
    defaultRequest { url(THE_MEAL_DB_BASE_URL) }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

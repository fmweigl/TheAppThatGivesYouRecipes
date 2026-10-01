package io.github.fmweigl.yetanothermealsapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val THE_MEAL_DB_BASE_URL = "https://www.themealdb.com/api/json/v2/"

/** TheMealDB's public key for development and tests. Released builds need a supporter key. */
private const val TEST_API_KEY = "1"

/**
 * Creates the app's client for TheMealDB. The engine comes from the platform (OkHttp on JVM, Darwin on
 * iOS). [THE_MEAL_DB_API_KEY] is generated at build time from the configured supporter key.
 */
internal fun createTheMealDbHttpClient(): HttpClient = HttpClient { theMealDbConfig(THE_MEAL_DB_API_KEY) }

/**
 * Creates a client for TheMealDB on [engine], configured like the app's client. Meant for tests with
 * a `MockEngine`; app code injects the shared [HttpClient] from `coreNetworkModule` instead. [apiKey]
 * defaults to the public test key, so request URLs in tests don't depend on the configured key.
 */
fun createTheMealDbHttpClient(engine: HttpClientEngine, apiKey: String = TEST_API_KEY): HttpClient =
    HttpClient(engine) { theMealDbConfig(apiKey) }

/** Relative request URLs resolve against TheMealDB's V2 API with [apiKey], and non-2xx responses throw. */
private fun HttpClientConfig<*>.theMealDbConfig(apiKey: String) {
    expectSuccess = true
    defaultRequest { url("$THE_MEAL_DB_BASE_URL$apiKey/") }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

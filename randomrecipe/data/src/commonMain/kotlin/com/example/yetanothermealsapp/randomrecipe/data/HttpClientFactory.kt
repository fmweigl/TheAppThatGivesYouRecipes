package com.example.yetanothermealsapp.randomrecipe.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val THE_MEAL_DB_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

/** Creates a client for TheMealDB. The engine comes from the platform (OkHttp on JVM, Darwin on iOS). */
internal fun createTheMealDbHttpClient(): HttpClient = HttpClient { theMealDbConfig() }

internal fun HttpClientConfig<*>.theMealDbConfig() {
    expectSuccess = true
    defaultRequest { url(THE_MEAL_DB_BASE_URL) }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

package io.github.fmweigl.theappthatgivesyourecipes.core.data

import io.github.fmweigl.theappthatgivesyourecipes.core.domain.DataError
import io.github.fmweigl.theappthatgivesyourecipes.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SafeApiCallTest {

    @Serializable
    private data class Body(val value: String)

    private fun client(handler: MockRequestHandler) = HttpClient(MockEngine(handler)) {
        expectSuccess = true
        install(ContentNegotiation) { json() }
    }

    private suspend fun call(handler: MockRequestHandler): Result<Body, DataError> {
        val client = client(handler)
        return safeApiCall { client.get("https://example.com").body<Body>() }
    }

    private fun respondJson(body: String, status: HttpStatusCode = HttpStatusCode.OK): MockRequestHandler = {
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    @Test
    fun returnsSuccess() = runTest {
        assertEquals(Result.Success(Body("ok")), call(respondJson("""{"value":"ok"}""")))
    }

    @Test
    fun mapsIoExceptionToNoConnection() = runTest {
        assertEquals(Result.Failure(DataError.NoConnection), call { throw IOException("offline") })
    }

    @Test
    fun mapsTimeoutsToTimeout() = runTest {
        val timeouts = listOf(
            HttpRequestTimeoutException("https://example.com", 1000),
            ConnectTimeoutException("connect timed out"),
            SocketTimeoutException("read timed out", null),
        )
        for (timeout in timeouts) {
            assertEquals(Result.Failure(DataError.Timeout), call { throw timeout }, "$timeout")
        }
    }

    @Test
    fun mapsServerErrorToServer() = runTest {
        assertEquals(Result.Failure(DataError.Server), call(respondJson("", HttpStatusCode.ServiceUnavailable)))
    }

    @Test
    fun mapsClientErrorToInvalidResponse() = runTest {
        assertEquals(Result.Failure(DataError.InvalidResponse), call(respondJson("", HttpStatusCode.NotFound)))
    }

    @Test
    fun mapsMalformedJsonToInvalidResponse() = runTest {
        assertEquals(Result.Failure(DataError.InvalidResponse), call(respondJson("""{"value":""")))
        assertEquals(Result.Failure(DataError.InvalidResponse), call(respondJson("""{"other":1}""")))
    }

    @Test
    fun mapsUnexpectedContentTypeToInvalidResponse() = runTest {
        val html: MockRequestHandler = {
            respond("<html></html>", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/html"))
        }
        assertEquals(Result.Failure(DataError.InvalidResponse), call(html))
    }

    @Test
    fun mapsSerializationExceptionToInvalidResponse() = runTest {
        val result = safeApiCall<Body> { throw SerializationException("bad") }
        assertEquals(Result.Failure(DataError.InvalidResponse), result)
    }

    @Test
    fun mapsOtherExceptionsToUnknown() = runTest {
        val result = safeApiCall<Body> { throw IllegalStateException("bug") }
        assertEquals(Result.Failure(DataError.Unknown), result)
    }

    @Test
    fun rethrowsCancellation() = runTest {
        assertFailsWith<CancellationException> {
            safeApiCall<Body> { throw CancellationException("cancelled") }
        }
    }
}

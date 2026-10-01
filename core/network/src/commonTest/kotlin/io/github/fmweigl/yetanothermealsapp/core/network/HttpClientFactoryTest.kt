package io.github.fmweigl.yetanothermealsapp.core.network

import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HttpClientFactoryTest {

    @Serializable
    private data class Body(val value: String)

    private fun respondingWith(body: String, status: HttpStatusCode = HttpStatusCode.OK) = MockEngine {
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    @Test
    fun resolvesRelativeUrlsAgainstTheMealDb() = runTest {
        val engine = respondingWith("""{"value":"ok"}""")
        createTheMealDbHttpClient(engine).get("random.php")

        assertEquals("https://www.themealdb.com/api/json/v1/1/random.php", engine.requestHistory.single().url.toString())
    }

    @Test
    fun ignoresUnknownJsonKeys() = runTest {
        val client = createTheMealDbHttpClient(respondingWith("""{"value":"ok","extra":1}"""))

        assertEquals(Body("ok"), client.get("random.php").body<Body>())
    }

    @Test
    fun throwsOnErrorStatus() = runTest {
        val client = createTheMealDbHttpClient(respondingWith("", HttpStatusCode.InternalServerError))

        assertFailsWith<ServerResponseException> { client.get("random.php") }
    }
}

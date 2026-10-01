package io.github.fmweigl.yetanothermealsapp.core.data

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Runs a Ktor request in [block] and maps its exceptions to [DataError], so no Ktor or
 * serialization types leave the data layer. Requires a client with `expectSuccess = true`,
 * otherwise non-2xx responses are not reported as errors.
 */
suspend inline fun <T> safeApiCall(block: () -> T): Result<T, DataError> =
    try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Failure(e.toDataError())
    }

/** Order matters: the timeout exceptions are [IOException]s too. */
@PublishedApi
internal fun Exception.toDataError(): DataError = when (this) {
    is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> DataError.Timeout
    is IOException -> DataError.NoConnection
    is ServerResponseException -> DataError.Server
    is ClientRequestException -> DataError.InvalidResponse
    is ContentConvertException, is SerializationException, is NoTransformationFoundException ->
        DataError.InvalidResponse
    else -> DataError.Unknown
}

package io.github.fmweigl.yetanothermealsapp.core.domain

/** Why loading data failed, independent of the library that loaded it. */
enum class DataError : Error {
    /** The device is offline or the server could not be reached. */
    NoConnection,

    /** The server did not respond in time. */
    Timeout,

    /** The server failed to handle the request (HTTP 5xx). */
    Server,

    /** The request was rejected (HTTP 4xx) or the response could not be understood. */
    InvalidResponse,

    /** The server has nothing for the request, such as a recipe id it doesn't know. */
    NotFound,

    Unknown,
}

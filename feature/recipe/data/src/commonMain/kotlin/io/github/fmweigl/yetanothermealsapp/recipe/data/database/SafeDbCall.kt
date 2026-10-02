package io.github.fmweigl.yetanothermealsapp.recipe.data.database

import androidx.sqlite.SQLiteException
import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result

/** Runs a database operation and maps its errors to [DataError.Storage], like `safeApiCall` does for requests. */
internal suspend fun <T> safeDbCall(operation: suspend () -> T): Result<T, DataError> =
    try {
        Result.Success(operation())
    } catch (_: SQLiteException) {
        Result.Failure(DataError.Storage)
    }

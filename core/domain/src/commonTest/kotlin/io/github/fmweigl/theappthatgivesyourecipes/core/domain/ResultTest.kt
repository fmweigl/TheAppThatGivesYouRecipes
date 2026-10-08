package io.github.fmweigl.theappthatgivesyourecipes.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResultTest {

    private val success: Result<Int, DataError> = Result.Success(2)
    private val failure: Result<Int, DataError> = Result.Failure(DataError.Server)

    @Test
    fun mapTransformsSuccessAndKeepsFailure() {
        assertEquals(Result.Success("2"), success.map { it.toString() })
        assertEquals(Result.Failure(DataError.Server), failure.map { it.toString() })
    }

    @Test
    fun flatMapChainsSuccessAndKeepsFailure() {
        assertEquals(Result.Success(4), success.flatMap { Result.Success(it * 2) })
        assertEquals(
            Result.Failure(DataError.InvalidResponse),
            success.flatMap { Result.Failure(DataError.InvalidResponse) },
        )
        assertEquals(failure, failure.flatMap { Result.Success(it * 2) })
    }

    @Test
    fun onSuccessAndOnFailureRunOnlyForMatchingCase() {
        var data: Int? = null
        var error: DataError? = null

        success.onSuccess { data = it }.onFailure { error = it }
        assertEquals(2, data)
        assertNull(error)

        data = null
        failure.onSuccess { data = it }.onFailure { error = it }
        assertNull(data)
        assertEquals(DataError.Server, error)
    }
}

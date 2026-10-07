package io.github.fmweigl.yetanothermealsapp.recipe.data.database

import androidx.sqlite.SQLiteException
import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.core.domain.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SafeDbCallTest {

    @Test
    fun returnsTheResult() = runTest {
        assertEquals(Result.Success(42), safeDbCall { 42 })
    }

    @Test
    fun mapsSqliteErrorsToStorage() = runTest {
        assertEquals(Result.Failure(DataError.Storage), safeDbCall { throw SQLiteException("disk I/O error") })
    }

    @Test
    fun rethrowsOtherErrors() = runTest {
        // Such as using a closed database: a bug, not a storage problem.
        assertFailsWith<IllegalStateException> {
            safeDbCall { error("Cannot perform this operation because the database is closed") }
        }
    }
}

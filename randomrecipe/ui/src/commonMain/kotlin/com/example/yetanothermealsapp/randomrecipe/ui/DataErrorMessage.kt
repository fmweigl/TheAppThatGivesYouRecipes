package com.example.yetanothermealsapp.randomrecipe.ui

import com.example.yetanothermealsapp.core.domain.DataError

/** The message shown to the user for this error. */
internal fun DataError.toMessage(): String = when (this) {
    DataError.NoConnection -> "Could not reach TheMealDB. Check your connection."
    DataError.Timeout -> "TheMealDB took too long to respond. Please try again."
    DataError.Server -> "TheMealDB is having trouble right now. Please try again later."
    DataError.InvalidResponse, DataError.Unknown -> "Something went wrong while loading the recipe."
}

package io.github.fmweigl.yetanothermealsapp.recipe.ui

import io.github.fmweigl.yetanothermealsapp.core.domain.DataError
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.Res
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.error_no_connection
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.error_not_found
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.error_server
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.error_storage
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.error_timeout
import io.github.fmweigl.yetanothermealsapp.recipe.ui.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

/** The message shown to the user for this error. */
internal fun DataError.toMessage(): StringResource = when (this) {
    DataError.NoConnection -> Res.string.error_no_connection
    DataError.Timeout -> Res.string.error_timeout
    DataError.Server -> Res.string.error_server
    DataError.NotFound -> Res.string.error_not_found
    DataError.Storage -> Res.string.error_storage
    DataError.InvalidResponse, DataError.Unknown -> Res.string.error_unknown
}

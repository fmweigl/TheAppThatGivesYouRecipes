package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect

/**
 * What to share: [subject] (the recipe's name; email apps use it), [text], and [anchor], the share
 * button's bounds in the window in pixels, which iPadOS needs to point its share popover at.
 */
internal class ShareRequest(val subject: String, val text: String, val anchor: Rect)

/** Hands text to the platform: the share sheet on Android and iOS, the clipboard on the desktop. */
internal fun interface Sharer {
    fun share(request: ShareRequest)
}

/**
 * Whether [Sharer] copies to the clipboard instead of opening a share sheet: only on the desktop,
 * which has none. The button is then labeled "Copy recipe" and confirms the copy.
 */
internal expect val shareCopiesToClipboard: Boolean

/** The platform's [Sharer]. */
@Composable
internal expect fun rememberPlatformSharer(): Sharer

/** Replaces the platform's [Sharer] in tests, which can't open share sheets or rely on a clipboard. */
internal val LocalSharer = staticCompositionLocalOf<Sharer?> { null }

/** The [Sharer] to use: [LocalSharer] if set, otherwise the platform's. */
@Composable
internal fun rememberSharer(): Sharer = LocalSharer.current ?: rememberPlatformSharer()

package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

internal actual val shareCopiesToClipboard: Boolean = true

/** The desktop has no share sheet, so the text goes to the system clipboard. */
@Composable
internal actual fun rememberPlatformSharer(): Sharer = remember {
    Sharer { request ->
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(request.text), null)
    }
}

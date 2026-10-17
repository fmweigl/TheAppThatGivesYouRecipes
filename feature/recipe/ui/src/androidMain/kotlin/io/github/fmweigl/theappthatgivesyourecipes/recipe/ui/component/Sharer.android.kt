package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

internal actual val shareCopiesToClipboard: Boolean = false

/** Android's share sheet, as plain text, with the subject for email apps. */
@Composable
internal actual fun rememberPlatformSharer(): Sharer {
    val context = LocalContext.current
    return remember(context) {
        Sharer { request ->
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, request.subject)
                putExtra(Intent.EXTRA_TEXT, request.text)
            }
            context.startActivity(Intent.createChooser(send, null))
        }
    }
}

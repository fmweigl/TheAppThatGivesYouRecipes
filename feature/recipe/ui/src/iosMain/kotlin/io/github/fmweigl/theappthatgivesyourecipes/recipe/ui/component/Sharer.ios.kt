package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController

internal actual val shareCopiesToClipboard: Boolean = false

/**
 * iOS's share sheet, presented from the top view controller. On iPad it's a popover, which needs a
 * source to point at (without one UIKit raises an exception): the share button, converted from
 * Compose's pixels to points (Compose's density on iOS is the screen's scale). The Compose view fills
 * the window (`ignoresSafeArea()` in `ContentView`), so window coordinates are the root view's.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun rememberPlatformSharer(): Sharer {
    val scale = LocalDensity.current.density
    return remember(scale) {
        Sharer { request ->
            val presenter = topViewController() ?: return@Sharer
            val controller = UIActivityViewController(
                activityItems = listOf(request.text),
                applicationActivities = null,
            )
            controller.popoverPresentationController?.let { popover ->
                popover.sourceView = presenter.view
                val points = request.anchor.let { Rect(it.topLeft / scale, it.size / scale) }
                popover.sourceRect = with(points) {
                    CGRectMake(left.toDouble(), top.toDouble(), width.toDouble(), height.toDouble())
                }
            }
            presenter.presentViewController(controller, animated = true, completion = null)
        }
    }
}

private fun topViewController(): UIViewController? {
    var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (controller?.presentedViewController != null) controller = controller.presentedViewController
    return controller
}

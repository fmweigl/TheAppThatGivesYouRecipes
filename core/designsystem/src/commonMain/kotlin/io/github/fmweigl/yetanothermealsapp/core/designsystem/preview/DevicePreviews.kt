package io.github.fmweigl.yetanothermealsapp.core.designsystem.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import io.github.fmweigl.yetanothermealsapp.core.designsystem.MealsTheme

private const val DARK = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL

/**
 * Previews a screen on a phone, a 7-inch and a 10-inch tablet, each in portrait and landscape, and
 * a small phone in landscape, in light and dark mode (14 previews). Wrap the content in
 * [MealsPreview], which follows the preview's dark mode. Sizes are the window sizes Material's size
 * classes are tested with: compact (phone), medium (600dp, 7-inch tablet portrait) and expanded
 * (840dp and up) widths; the small phone in landscape is a medium width with a compact height
 * (under 480dp), which the larger phone (891dp wide in landscape) doesn't cover.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Phone", group = "Phone", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Phone, dark", group = "Phone", device = "spec:width=411dp,height=891dp,dpi=420", uiMode = DARK)
@Preview(
    name = "Phone landscape",
    group = "Phone",
    device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape",
)
@Preview(
    name = "Phone landscape, dark",
    group = "Phone",
    device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape",
    uiMode = DARK,
)
@Preview(
    name = "Small phone landscape",
    group = "Phone",
    device = "spec:width=360dp,height=640dp,dpi=320,orientation=landscape",
)
@Preview(
    name = "Small phone landscape, dark",
    group = "Phone",
    device = "spec:width=360dp,height=640dp,dpi=320,orientation=landscape",
    uiMode = DARK,
)
@Preview(name = "7-inch tablet", group = "7-inch tablet", device = "spec:width=600dp,height=960dp,dpi=320")
@Preview(
    name = "7-inch tablet, dark",
    group = "7-inch tablet",
    device = "spec:width=600dp,height=960dp,dpi=320",
    uiMode = DARK,
)
@Preview(
    name = "7-inch tablet landscape",
    group = "7-inch tablet",
    device = "spec:width=600dp,height=960dp,dpi=320,orientation=landscape",
)
@Preview(
    name = "7-inch tablet landscape, dark",
    group = "7-inch tablet",
    device = "spec:width=600dp,height=960dp,dpi=320,orientation=landscape",
    uiMode = DARK,
)
@Preview(name = "10-inch tablet", group = "10-inch tablet", device = "spec:width=800dp,height=1280dp,dpi=240")
@Preview(
    name = "10-inch tablet, dark",
    group = "10-inch tablet",
    device = "spec:width=800dp,height=1280dp,dpi=240",
    uiMode = DARK,
)
@Preview(
    name = "10-inch tablet landscape",
    group = "10-inch tablet",
    device = "spec:width=800dp,height=1280dp,dpi=240,orientation=landscape",
)
@Preview(
    name = "10-inch tablet landscape, dark",
    group = "10-inch tablet",
    device = "spec:width=800dp,height=1280dp,dpi=240,orientation=landscape",
    uiMode = DARK,
)
annotation class DevicePreviews

/** [MealsTheme] (light or dark, following the preview's dark mode) on the theme's background. */
@Composable
fun MealsPreview(content: @Composable () -> Unit) {
    MealsTheme {
        Surface(content = content)
    }
}

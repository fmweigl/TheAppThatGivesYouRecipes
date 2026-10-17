package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** The test window's density is 1, so dp are px. */
@OptIn(ExperimentalTestApi::class)
class ShareButtonTest {

    @Test
    fun hasTheHeartsSizeAndIsLabeledAsAButton() = runComposeUiTest {
        setContent {
            Row {
                ShareButton(onClick = {}, contentDescription = "Share recipe")
                FavoriteButton(isFavorite = false, onToggle = {})
            }
        }

        val share = onNodeWithContentDescription("Share recipe")
        share.assertHasClickAction()
        assertEquals(
            onNodeWithContentDescription("Favorite").fetchSemanticsNode().size,
            share.fetchSemanticsNode().size,
        )
        assertEquals(56, share.fetchSemanticsNode().size.width)
    }

    @Test
    fun keyboardFocusDrawsARing() = runComposeUiTest {
        setContent { ShareButton(onClick = {}, contentDescription = "Share recipe") }
        val button = onNodeWithContentDescription("Share recipe")

        fun edgePixel() = button.captureToImage().toPixelMap().let { it[it.width / 2, 1] }
        val before = edgePixel()
        onRoot().performKeyInput { pressKey(Key.Tab) }
        waitForIdle()

        assertNotEquals(before, edgePixel())
    }
}

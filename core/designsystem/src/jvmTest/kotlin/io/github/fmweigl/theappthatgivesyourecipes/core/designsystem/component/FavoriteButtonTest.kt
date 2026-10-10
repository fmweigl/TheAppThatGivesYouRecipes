package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Only a tap animates the heart; the test window's density is 1, so the 32dp heart is 32px wide. */
@OptIn(ExperimentalTestApi::class)
class FavoriteButtonTest {

    private val heartSize = 32f

    /** The heart's drawn width, which includes the pop's scale. */
    private fun ComposeUiTest.heartWidth() =
        onNodeWithContentDescription("Favorite", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot.width

    private fun ComposeUiTest.advanceFrames(count: Int) = repeat(count) { mainClock.advanceTimeByFrame() }

    @Test
    fun changeFromElsewhereDoesNotAnimate() = runComposeUiTest {
        // As after "Next" onto a saved recipe: shown empty, then the database answers.
        var isFavorite by mutableStateOf(false)
        setContent { FavoriteButton(isFavorite = isFavorite, onToggle = {}) }
        mainClock.autoAdvance = false

        isFavorite = true
        advanceFrames(2)

        assertEquals(heartSize, heartWidth())
    }

    @Test
    fun tapAnimatesThenSettles() = runComposeUiTest {
        var isFavorite by mutableStateOf(false)
        setContent { FavoriteButton(isFavorite = isFavorite, onToggle = { isFavorite = !isFavorite }) }
        mainClock.autoAdvance = false

        onNodeWithContentDescription("Favorite").performClick()
        advanceFrames(2)
        assertTrue(heartWidth() < heartSize, "the heart pops from smaller than its size")

        mainClock.advanceTimeBy(1_000)
        assertEquals(heartSize, heartWidth())
    }

    @Test
    fun keyboardFocusDrawsARing() = runComposeUiTest {
        setContent { FavoriteButton(isFavorite = false, onToggle = {}) }
        val button = onNodeWithContentDescription("Favorite")

        // Next to the button's edge, where nothing else is drawn unless the ring is.
        fun edgePixel() = button.captureToImage().toPixelMap().let { it[it.width / 2, 1] }
        val before = edgePixel()
        onRoot().performKeyInput { pressKey(Key.Tab) }
        waitForIdle()

        assertNotEquals(before, edgePixel())
    }
}

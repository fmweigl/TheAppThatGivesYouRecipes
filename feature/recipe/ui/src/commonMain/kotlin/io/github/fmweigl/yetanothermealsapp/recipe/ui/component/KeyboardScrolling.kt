package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Whether scrolling panes without focusable content take keyboard focus themselves. Only on the
 * desktop: on Android and iOS a focusable pane of plain text risks the screen reader reading it as
 * one block instead of row by row.
 */
internal expect val panesScrollWithKeyboard: Boolean

/** Fraction of the pane's height that PageUp/PageDown scroll, keeping a few lines of context. */
private const val PAGE_FRACTION = 0.8f

/** Distance in dp the arrow keys scroll. */
private const val ARROW_STEP_DP = 48

/**
 * Lets the keyboard scroll a pane that has nothing focusable inside it (Compose only scrolls from
 * the keyboard when focus is within the pane): the pane takes focus with Tab, shows a border while
 * focused and scrolls with PageUp/PageDown and the arrow keys. Does nothing where
 * [panesScrollWithKeyboard] is false.
 */
@Composable
internal fun Modifier.keyboardScrollable(state: ScrollableState): Modifier {
    if (!panesScrollWithKeyboard) return this
    val scope = rememberCoroutineScope()
    var isFocused by remember { mutableStateOf(false) }
    var height by remember { mutableIntStateOf(0) }
    val arrowStep = with(LocalDensity.current) { ARROW_STEP_DP.dp.toPx() }
    val focusBorder = if (isFocused) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
    } else {
        Modifier
    }
    return this
        .onSizeChanged { height = it.height }
        .onFocusChanged { isFocused = it.isFocused }
        .then(focusBorder)
        .onKeyEvent { event ->
            val distance = when (event.key) {
                Key.PageDown -> height * PAGE_FRACTION
                Key.PageUp -> -height * PAGE_FRACTION
                Key.DirectionDown -> arrowStep
                Key.DirectionUp -> -arrowStep
                else -> return@onKeyEvent false
            }
            if (event.type == KeyEventType.KeyDown) scope.launch { state.animateScrollBy(distance) }
            true
        }
        .focusable()
}

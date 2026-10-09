package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics

/** How long the recipe screens fade from one content to the next: short, so loading never feels slower. */
internal const val CONTENT_FADE_MILLIS = 250

/**
 * Shows [content] for [targetState] and fades to the next content (skeleton, error, recipe, the next
 * recipe) when [contentKey] changes; a state with the same key, such as a heart that changed,
 * updates the content in place. The content fading out keeps its state from before, but is gone
 * at once for screen readers, touch and keyboard focus, so nobody acts on what is leaving (its
 * heart would change the recipe that's coming).
 */
@Composable
internal fun <S> RecipeContentFade(
    targetState: S,
    contentKey: (S) -> Any?,
    modifier: Modifier = Modifier,
    content: @Composable (S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = { fadeIn(tween(CONTENT_FADE_MILLIS)) togetherWith fadeOut(tween(CONTENT_FADE_MILLIS)) },
        contentKey = contentKey,
    ) { state ->
        val leaving = transition.targetState == EnterExitState.PostExit
        Box(if (leaving) LeavingModifier else Modifier) {
            content(state)
            // On top of the content, so it gets every touch on it.
            if (leaving) Box(Modifier.matchParentSize().pointerInput(Unit) { consumeAllPointerEvents() })
        }
    }
}

/** Takes the content out of the semantics tree and keeps keyboard focus from moving into it. */
private val LeavingModifier = Modifier
    .clearAndSetSemantics {}
    .focusProperties { onEnter = { cancelFocusChange() } }
    .focusGroup()

private suspend fun PointerInputScope.consumeAllPointerEvents() {
    awaitPointerEventScope {
        while (true) awaitPointerEvent().changes.forEach { it.consume() }
    }
}

package io.github.fmweigl.yetanothermealsapp.navigation

import androidx.navigation3.runtime.NavKey

/** Handles navigation events by updating [state]. */
internal class Navigator(private val state: NavigationState) {

    /** Switches to [route] if it is a top level route, otherwise pushes it onto the current stack. */
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            currentStack().add(route)
        }
    }

    /** Pops the current stack, or returns to the start route from the root of another tab. */
    fun goBack() {
        val currentStack = currentStack()
        if (currentStack.last() == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    private fun currentStack() =
        state.backStacks[state.topLevelRoute] ?: error("No back stack for ${state.topLevelRoute}")
}

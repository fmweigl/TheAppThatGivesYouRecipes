package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

/**
 * Whether the system asks apps to avoid motion and the app has to check it itself: iOS's Reduce
 * Motion. Android needs no check (its animation scale, which "Remove animations" sets to 0, already
 * applies to Compose animations), and the desktop has no such setting.
 */
internal expect fun prefersReducedMotion(): Boolean

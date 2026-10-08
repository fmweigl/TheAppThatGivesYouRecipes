package io.github.fmweigl.yetanothermealsapp.recipe.ui.component

import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

internal actual fun prefersReducedMotion(): Boolean = UIAccessibilityIsReduceMotionEnabled()

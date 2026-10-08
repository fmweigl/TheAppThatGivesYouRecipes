package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

internal actual fun prefersReducedMotion(): Boolean = UIAccessibilityIsReduceMotionEnabled()

package io.github.fmweigl.yetanothermealsapp.core.designsystem.component

import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

internal actual fun prefersReducedMotion(): Boolean = UIAccessibilityIsReduceMotionEnabled()

package io.github.fmweigl.theappthatgivesyourecipes.recipe.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest

// The window sizes the recipe screens' tests run at (in dp: the test window's density is 1). The
// default test window (1024×768) would already get the two-pane layout.

/** A phone in portrait: compact width, one column. */
@OptIn(ExperimentalTestApi::class)
internal fun runPhoneTest(block: suspend ComposeUiTest.() -> Unit) =
    runDesktopComposeUiTest(width = 411, height = 891) { block() }

/** A 10-inch tablet in portrait: medium width, the centered column. */
@OptIn(ExperimentalTestApi::class)
internal fun runTabletPortraitTest(block: suspend ComposeUiTest.() -> Unit) =
    runDesktopComposeUiTest(width = 800, height = 1280) { block() }

/** A 10-inch tablet in landscape: expanded width, two panes. */
@OptIn(ExperimentalTestApi::class)
internal fun runTabletLandscapeTest(block: suspend ComposeUiTest.() -> Unit) =
    runDesktopComposeUiTest(width = 1280, height = 800) { block() }

/** A small phone in landscape: medium width with a compact height, two panes. */
@OptIn(ExperimentalTestApi::class)
internal fun runSmallPhoneLandscapeTest(block: suspend ComposeUiTest.() -> Unit) =
    runDesktopComposeUiTest(width = 640, height = 360) { block() }

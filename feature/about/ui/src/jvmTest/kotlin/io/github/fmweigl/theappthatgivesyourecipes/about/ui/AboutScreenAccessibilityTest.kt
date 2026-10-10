package io.github.fmweigl.theappthatgivesyourecipes.about.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotFocusable
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runPhoneTest
import io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.test.runSmallPhoneLandscapeTest
import kotlin.test.Test

/** What screen readers get from [AboutScreen] (the merged semantics tree). */
@OptIn(ExperimentalTestApi::class)
class AboutScreenAccessibilityTest {

    @Test
    fun theVersionIsOneNodeThatIsNeitherHeadingNorClickable() = runPhoneTest {
        setContent { AboutScreen(onNavigate = {}, versionName = "1.0.42", versionCode = 42) }

        onAllNodesWithText("Version 1.0.42 (42)").assertCountEquals(1)
        onNodeWithText("Version 1.0.42 (42)")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
            .assert(isNotFocusable())
            .assertHasNoClickAction()
    }

    @Test
    fun theVersionCanBeScrolledToInAShortWindowWithLargeText() = runSmallPhoneLandscapeTest {
        setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                AboutScreen(onNavigate = {}, versionName = "1.0.42", versionCode = 42)
            }
        }

        onNodeWithText("Version 1.0.42 (42)").performScrollTo().assertIsDisplayed()
    }
}

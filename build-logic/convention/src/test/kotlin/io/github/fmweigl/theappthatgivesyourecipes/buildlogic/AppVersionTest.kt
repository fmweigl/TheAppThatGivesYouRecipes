package io.github.fmweigl.theappthatgivesyourecipes.buildlogic

import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppVersionTest {

    @Test
    fun theNameIsOneDotZeroDotCode() {
        assertEquals("1.0.42", AppVersion.fromProperty("42").name)
        assertEquals(42, AppVersion.fromProperty("42").code)
    }

    @Test
    fun withoutThePropertyTheCodeIsOne() {
        for (missing in listOf(null, "", "  ")) {
            val version = AppVersion.fromProperty(missing)
            assertEquals(1, version.code)
            assertEquals("1.0.1", version.name)
        }
    }

    @Test
    fun aCodeThatIsNotAPositiveNumberFails() {
        for (invalid in listOf("abc", "0", "-3", "1.5")) {
            assertFailsWith<GradleException> { AppVersion.fromProperty(invalid) }
        }
    }

    @Test
    fun desktopAcceptsCodesUpToTheMsiLimit() {
        AppVersion(MAX_DESKTOP_VERSION_CODE).requireDesktopPackagable()
    }

    @Test
    fun desktopRejectsCodesAboveTheMsiLimit() {
        val error = assertFailsWith<GradleException> {
            AppVersion(MAX_DESKTOP_VERSION_CODE + 1).requireDesktopPackagable()
        }
        assertTrue("65535" in error.message.orEmpty())
    }

    @Test
    fun theMsiVersionIsTheNameUpToTheLimitAndAValidPlaceholderAboveIt() {
        assertEquals("1.0.42", AppVersion(42).msiPackageVersion)
        assertEquals("1.0.65535", AppVersion(MAX_DESKTOP_VERSION_CODE).msiPackageVersion)
        assertEquals("1.0.65535", AppVersion(MAX_DESKTOP_VERSION_CODE + 1).msiPackageVersion)
    }

    @Test
    fun theIosConfigHasNameAndCode() {
        val lines = AppVersion(42).toXcconfig().lines()

        assertTrue("MARKETING_VERSION = 1.0.42" in lines)
        assertTrue("CURRENT_PROJECT_VERSION = 42" in lines)
    }
}

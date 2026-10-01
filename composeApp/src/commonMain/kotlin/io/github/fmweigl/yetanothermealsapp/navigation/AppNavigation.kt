package io.github.fmweigl.yetanothermealsapp.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import io.github.fmweigl.yetanothermealsapp.about.ui.AboutContent
import io.github.fmweigl.yetanothermealsapp.about.ui.AboutNavKey
import io.github.fmweigl.yetanothermealsapp.about.ui.AttributionsNavKey
import io.github.fmweigl.yetanothermealsapp.about.ui.LibrariesNavKey
import io.github.fmweigl.yetanothermealsapp.about.ui.LicenseNavKey
import io.github.fmweigl.yetanothermealsapp.about.ui.PrivacyNavKey
import io.github.fmweigl.yetanothermealsapp.about.ui.aboutEntries
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.RandomRecipeNavKey
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.randomRecipeEntry
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import yetanothermealsapp.composeapp.generated.resources.Res
import yetanothermealsapp.composeapp.generated.resources.tab_about
import yetanothermealsapp.composeapp.generated.resources.tab_random

private class TopLevelDestination(val icon: ImageVector, val label: StringResource)

/** The tabs of the bottom navigation bar, in display order. The first one is the start route. */
private val topLevelDestinations: Map<NavKey, TopLevelDestination> = linkedMapOf(
    RandomRecipeNavKey to TopLevelDestination(Icons.Filled.Refresh, Res.string.tab_random),
    AboutNavKey to TopLevelDestination(Icons.Filled.Info, Res.string.tab_about),
)

/** Registers every [NavKey] for saving the back stacks; add new keys here. */
private val navKeyConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(RandomRecipeNavKey::class)
            subclass(AboutNavKey::class)
            subclass(LicenseNavKey::class)
            subclass(PrivacyNavKey::class)
            subclass(AttributionsNavKey::class)
            subclass(LibrariesNavKey::class)
        }
    }
}

/** The about screens' content: files that generateAppComposeResources copies into the resources. */
private val aboutContent = AboutContent(
    loadLicenseText = { Res.readBytes("files/LICENSE").decodeToString() },
    loadPrivacyText = { Res.readBytes("files/PRIVACY.md").decodeToString() },
    loadAttributionsText = { Res.readBytes("files/ATTRIBUTIONS.md").decodeToString() },
    loadLibrariesJson = { Res.readBytes("files/aboutlibraries.json").decodeToString() },
)

/** The app's bottom navigation bar and the screen of the selected tab. */
@Composable
internal fun AppNavigation(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(
        startRoute = topLevelDestinations.keys.first(),
        topLevelRoutes = topLevelDestinations.keys,
        configuration = navKeyConfiguration,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val entryProvider = entryProvider {
        randomRecipeEntry()
        aboutEntries(
            onNavigate = navigator::navigate,
            onBack = navigator::goBack,
            content = aboutContent,
        )
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { (route, destination) ->
                    NavigationBarItem(
                        selected = route == navigationState.topLevelRoute,
                        onClick = { navigator.navigate(route) },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(stringResource(destination.label)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = navigator::goBack,
            // Consumed, so screens with their own top bar don't add the status bar padding again.
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
        )
    }
}

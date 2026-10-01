package io.github.fmweigl.yetanothermealsapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
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
import io.github.fmweigl.yetanothermealsapp.helloworld.HelloWorldNavKey
import io.github.fmweigl.yetanothermealsapp.helloworld.helloWorldEntry
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.RandomRecipeNavKey
import io.github.fmweigl.yetanothermealsapp.randomrecipe.ui.randomRecipeEntry
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

private class TopLevelDestination(val icon: ImageVector, val label: String)

/** The tabs of the bottom navigation bar, in display order. The first one is the start route. */
private val topLevelDestinations: Map<NavKey, TopLevelDestination> = linkedMapOf(
    RandomRecipeNavKey to TopLevelDestination(Icons.Filled.Refresh, "Random"),
    HelloWorldNavKey to TopLevelDestination(Icons.Filled.Face, "Hello"),
)

/** Registers every [NavKey] for saving the back stacks; add new keys here. */
private val navKeyConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(RandomRecipeNavKey::class)
            subclass(HelloWorldNavKey::class)
        }
    }
}

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
        helloWorldEntry()
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
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = navigator::goBack,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

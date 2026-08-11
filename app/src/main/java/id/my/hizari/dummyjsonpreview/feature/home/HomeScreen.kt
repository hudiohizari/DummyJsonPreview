package id.my.hizari.dummyjsonpreview.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.my.hizari.dummyjsonpreview.navigation.AppDestinations
import id.my.hizari.dummyjsonpreview.navigation.AppTransitions
import id.my.hizari.dummyjsonpreview.navigation.Screen
import id.my.hizari.dummyjsonpreview.navigation.graph.addProductGraph
import id.my.hizari.dummyjsonpreview.navigation.graph.cartsGraph
import id.my.hizari.dummyjsonpreview.navigation.graph.categoriesGraph
import id.my.hizari.dummyjsonpreview.navigation.graph.productGraph
import id.my.hizari.dummyjsonpreview.navigation.graph.profileGraph
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.home
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // The active tab is derived from the destination's parent graph, which is why every tab is
    // wrapped in its own nested graph.
    val currentDestination = AppDestinations.entries.find(
        predicate = { it.graphRoute == navBackStackEntry?.destination?.parent?.route }
    ) ?: AppDestinations.PRODUCT

    // Visited tabs, most recent last, so back walks tab history instead of leaving the app.
    val tabHistory = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { mutableStateListOf(*it.toTypedArray()) }
        ),
        init = { mutableStateListOf(Screen.ProductGraph.route) }
    )

    HomeScaffold(
        modifier = modifier,
        currentDestination = currentDestination,
        onDestinationClick = { destination ->
            if (destination == currentDestination) {
                navController.popBackStack(route = destination.rootRoute, inclusive = false)
            } else {
                tabHistory.remove(element = destination.graphRoute)
                tabHistory.add(element = destination.graphRoute)
                navController.navigate(route = destination.graphRoute) {
                    popUpTo(id = navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Screen.ProductGraph.route,
            enterTransition = AppTransitions.Enter,
            exitTransition = AppTransitions.Exit,
            popEnterTransition = AppTransitions.PopEnter,
            popExitTransition = AppTransitions.PopExit,
            builder = {
                productGraph(navController = navController)
                categoriesGraph()
                addProductGraph()
                cartsGraph()
                profileGraph()
            }
        )

        // Registered after the NavHost, so it outranks the inner controller's own back handling
        // while it is enabled.
        val isAtTabRoot = navBackStackEntry?.destination?.id ==
            navBackStackEntry?.destination?.parent?.findStartDestination()?.id
        BackHandler(
            enabled = tabHistory.size > 1 && isAtTabRoot,
            onBack = {
                tabHistory.removeAt(index = tabHistory.lastIndex)
                val previousTab = tabHistory.lastOrNull() ?: Screen.ProductGraph.route
                navController.navigate(route = previousTab) {
                    popUpTo(id = navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

/**
 * The bottom navigation shell, kept free of the graph so it can be previewed. Rendering the real
 * [HomeScreen] would compose the tab graphs, and those reach for a view model the preview renderer
 * has no activity to build.
 */
@Composable
fun HomeScaffold(
    modifier: Modifier = Modifier,
    currentDestination: AppDestinations,
    onDestinationClick: (AppDestinations) -> Unit,
    content: @Composable () -> Unit
) {
    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = Color.Transparent
        )
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        // Pinned so a tablet or landscape window keeps the bottom bar instead of a navigation rail.
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    selected = destination == currentDestination,
                    onClick = { onDestinationClick(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = stringResource(id = destination.labelRes)
                        )
                    },
                    label = { Text(text = stringResource(id = destination.labelRes)) },
                    colors = itemColors
                )
            }
        },
        layoutType = NavigationSuiteType.NavigationBar,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        insets = WindowInsets.safeDrawing.only(sides = WindowInsetsSides.Top)
                    ),
                content = { content() }
            )
        }
    )
}

@Preview(name = "Home shell", showBackground = true)
@Composable
private fun HomeScaffoldPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        HomeScaffold(
            currentDestination = AppDestinations.PRODUCT,
            onDestinationClick = {},
            content = {}
        )
    }
}

@Preview(name = "Home shell on profile", showBackground = true)
@Composable
private fun HomeScaffoldProfilePreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        HomeScaffold(
            currentDestination = AppDestinations.PROFILE,
            onDestinationClick = {},
            content = {}
        )
    }
}

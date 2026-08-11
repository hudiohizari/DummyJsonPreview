package id.my.hizari.dummyjsonpreview.navigation.graph

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.my.hizari.dummyjsonpreview.feature.home.HomeScreen
import id.my.hizari.dummyjsonpreview.feature.login.LoginScreen
import id.my.hizari.dummyjsonpreview.navigation.Screen

/**
 * id.my.hizari.dummyjsonpreview.navigation.graph
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Login lives outside the bottom navigation on purpose. Sharing one NavHost would make the login
 * destination the graph's start destination, which the tab-switch popUpTo targets, and back from a
 * tab would land on login while still signed in.
 *
 * Navigation is driven entirely by the stored session, so signing in and signing out both move the
 * user without any screen needing a callback.
 */
@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean,
    startsAuthenticated: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(key1 = isAuthenticated, key2 = currentRoute) {
        val target = if (isAuthenticated) Screen.Main.route else Screen.Login.route
        if (currentRoute != null && currentRoute != target) {
            navController.navigate(route = target) {
                // Clearing the whole stack is what makes back exit the app rather than return to
                // the screens of a session that has ended.
                popUpTo(id = navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = if (startsAuthenticated) Screen.Main.route else Screen.Login.route,
        enterTransition = { fadeIn(animationSpec = tween(durationMillis = 220)) },
        exitTransition = { fadeOut(animationSpec = tween(durationMillis = 220)) },
        builder = {
            composable(route = Screen.Login.route) {
                LoginScreen(modifier = Modifier.fillMaxSize())
            }
            composable(route = Screen.Main.route) {
                HomeScreen(modifier = Modifier.fillMaxSize())
            }
        }
    )
}

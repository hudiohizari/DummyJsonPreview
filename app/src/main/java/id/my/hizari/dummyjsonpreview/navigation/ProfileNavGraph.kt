package id.my.hizari.dummyjsonpreview.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.my.hizari.dummyjsonpreview.feature.profile.ProfileScreen

/**
 * id.my.hizari.dummyjsonpreview.navigation
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun NavGraphBuilder.profileGraph() {
    navigation(
        startDestination = Screen.Profile.route,
        route = Screen.ProfileGraph.route
    ) {
        composable(route = Screen.Profile.route) {
            ProfileScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

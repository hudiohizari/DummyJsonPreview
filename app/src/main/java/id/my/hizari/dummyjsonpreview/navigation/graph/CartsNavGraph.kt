package id.my.hizari.dummyjsonpreview.navigation.graph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.feature.comingsoon.ComingSoonScreen
import id.my.hizari.dummyjsonpreview.navigation.Screen

/**
 * id.my.hizari.dummyjsonpreview.navigation.graph
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun NavGraphBuilder.cartsGraph() {
    navigation(
        startDestination = Screen.Carts.route,
        route = Screen.CartsGraph.route
    ) {
        composable(route = Screen.Carts.route) {
            ComingSoonScreen(
                modifier = Modifier.fillMaxSize(),
                titleRes = R.string.nav_carts
            )
        }
    }
}

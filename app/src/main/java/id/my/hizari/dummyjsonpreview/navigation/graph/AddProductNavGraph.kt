package id.my.hizari.dummyjsonpreview.navigation.graph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.my.hizari.dummyjsonpreview.feature.addproduct.AddProductScreen
import id.my.hizari.dummyjsonpreview.navigation.Screen

/**
 * id.my.hizari.dummyjsonpreview.navigation.graph
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun NavGraphBuilder.addProductGraph() {
    navigation(
        startDestination = Screen.AddProduct.route,
        route = Screen.AddProductGraph.route
    ) {
        composable(route = Screen.AddProduct.route) {
            AddProductScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

package id.my.hizari.dummyjsonpreview.navigation.graph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import id.my.hizari.dummyjsonpreview.feature.product.detail.ProductDetailScreen
import id.my.hizari.dummyjsonpreview.feature.product.edit.ProductEditScreen
import id.my.hizari.dummyjsonpreview.feature.product.list.ProductListScreen
import id.my.hizari.dummyjsonpreview.navigation.NavigationArgs
import id.my.hizari.dummyjsonpreview.navigation.Screen

/**
 * id.my.hizari.dummyjsonpreview.navigation.graph
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun NavGraphBuilder.productGraph(navController: NavController) {
    navigation(
        startDestination = Screen.ProductList.route,
        route = Screen.ProductGraph.route
    ) {
        composable(route = Screen.ProductList.route) {
            ProductListScreen(
                modifier = Modifier.fillMaxSize(),
                onProductClick = { productId ->
                    navController.navigate(
                        route = Screen.ProductDetail.createRoute(productId = productId)
                    )
                }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.PRODUCT_ID, builder = { type = NavType.IntType })
            )
        ) {
            // The id reaches the view model through its SavedStateHandle, so it is not passed here.
            ProductDetailScreen(
                modifier = Modifier.fillMaxSize(),
                onEditClick = { productId ->
                    navController.navigate(
                        route = Screen.ProductEdit.createRoute(productId = productId)
                    )
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ProductEdit.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.PRODUCT_ID, builder = { type = NavType.IntType })
            )
        ) { backStackEntry ->
            ProductEditScreen(
                modifier = Modifier.fillMaxSize(),
                productId = backStackEntry.arguments?.getInt(NavigationArgs.PRODUCT_ID) ?: 0
            )
        }
    }
}

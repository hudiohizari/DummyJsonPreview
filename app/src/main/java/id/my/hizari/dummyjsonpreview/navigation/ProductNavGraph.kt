package id.my.hizari.dummyjsonpreview.navigation

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

/**
 * id.my.hizari.dummyjsonpreview.navigation
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
                onProductClick = { productId ->
                    navController.navigate(
                        route = Screen.ProductDetail.createRoute(productId = productId)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.PRODUCT_ID, builder = { type = NavType.IntType })
            )
        ) { backStackEntry ->
            ProductDetailScreen(
                productId = backStackEntry.arguments?.getInt(NavigationArgs.PRODUCT_ID) ?: 0,
                onEditClick = { productId ->
                    navController.navigate(
                        route = Screen.ProductEdit.createRoute(productId = productId)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = Screen.ProductEdit.route,
            arguments = listOf(
                navArgument(name = NavigationArgs.PRODUCT_ID, builder = { type = NavType.IntType })
            )
        ) { backStackEntry ->
            ProductEditScreen(
                productId = backStackEntry.arguments?.getInt(NavigationArgs.PRODUCT_ID) ?: 0,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

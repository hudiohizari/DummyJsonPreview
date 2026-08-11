package id.my.hizari.dummyjsonpreview.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import id.my.hizari.dummyjsonpreview.R

/**
 * id.my.hizari.dummyjsonpreview.navigation
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * rootRoute is what a re-tap of the active tab pops back to, so it has to be the tab's start
 * destination rather than its graph.
 */
enum class AppDestinations(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val graphRoute: String,
    val rootRoute: String
) {
    PRODUCT(
        labelRes = R.string.nav_products,
        icon = Icons.Default.Storefront,
        graphRoute = Screen.ProductGraph.route,
        rootRoute = Screen.ProductList.route
    ),
    CATEGORIES(
        labelRes = R.string.nav_categories,
        icon = Icons.Default.Category,
        graphRoute = Screen.CategoriesGraph.route,
        rootRoute = Screen.Categories.route
    ),
    ADD_PRODUCT(
        labelRes = R.string.nav_add,
        icon = Icons.Default.AddBox,
        graphRoute = Screen.AddProductGraph.route,
        rootRoute = Screen.AddProduct.route
    ),
    CARTS(
        labelRes = R.string.nav_carts,
        icon = Icons.Default.ShoppingCart,
        graphRoute = Screen.CartsGraph.route,
        rootRoute = Screen.Carts.route
    ),
    PROFILE(
        labelRes = R.string.nav_profile,
        icon = Icons.Default.AccountCircle,
        graphRoute = Screen.ProfileGraph.route,
        rootRoute = Screen.Profile.route
    )
}

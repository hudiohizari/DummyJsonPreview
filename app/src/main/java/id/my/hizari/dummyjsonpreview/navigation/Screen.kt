package id.my.hizari.dummyjsonpreview.navigation

/**
 * id.my.hizari.dummyjsonpreview.navigation
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

sealed class Screen(val route: String) {

    /* Root graph, outside the bottom navigation. */
    data object Login : Screen(route = "login")
    data object Main : Screen(route = "main")

    /* One nested graph per tab. A tab whose destination is not wrapped in its own graph would
       report the root graph as its parent, and the selected-tab lookup would silently fall back. */
    data object ProductGraph : Screen(route = "product_graph")
    data object CategoriesGraph : Screen(route = "categories_graph")
    data object AddProductGraph : Screen(route = "add_product_graph")
    data object CartsGraph : Screen(route = "carts_graph")
    data object ProfileGraph : Screen(route = "profile_graph")

    /* Tab roots. */
    data object ProductList : Screen(route = "product_list")
    data object Categories : Screen(route = "categories")
    data object AddProduct : Screen(route = "add_product")
    data object Carts : Screen(route = "carts")
    data object Profile : Screen(route = "profile")

    /* Only the product tab pushes these, so a single route each is correct. If another tab ever
       opens a product, give it its own prefixed route rather than sharing this one. */
    data object ProductDetail : Screen(route = "product_detail/{${NavigationArgs.PRODUCT_ID}}") {
        fun createRoute(productId: Int): String = "product_detail/$productId"
    }

    data object ProductEdit : Screen(route = "product_edit/{${NavigationArgs.PRODUCT_ID}}") {
        fun createRoute(productId: Int): String = "product_edit/$productId"
    }
}

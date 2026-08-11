package id.my.hizari.dummyjsonpreview.domain.product.model

/**
 * id.my.hizari.dummyjsonpreview.domain.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ProductPage(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
) {
    // An empty page also means the end, otherwise pagination would spin on a server that keeps
    // reporting a total it will not actually serve.
    val hasMore: Boolean
        get() = products.isNotEmpty() && skip + products.size < total
}

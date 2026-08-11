package id.my.hizari.dummyjsonpreview.feature.product.list

import id.my.hizari.dummyjsonpreview.domain.product.model.Product

/**
 * id.my.hizari.dummyjsonpreview.feature.product.list
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ProductListState(
    val greetingName: String = "",
    val query: String = "",
    val products: List<Product> = emptyList(),
    val total: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val paginationErrorMessage: String? = null
) {
    /** An empty result is a normal outcome of a search, not a failure. */
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && products.isEmpty()

    /** A failure only takes over the screen when there is nothing to fall back on. */
    val showFullScreenError: Boolean
        get() = errorMessage != null && products.isEmpty()
}

package id.my.hizari.dummyjsonpreview.feature.product.detail

import id.my.hizari.dummyjsonpreview.domain.product.model.Product

/**
 * id.my.hizari.dummyjsonpreview.feature.product.detail
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ProductDetailState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDeleteDialogVisible: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteErrorMessage: String? = null,
    val deletedTitle: String? = null
) {
    /** A failure only takes over the screen when there is no product to fall back on. */
    val showFullScreenError: Boolean
        get() = errorMessage != null && product == null
}

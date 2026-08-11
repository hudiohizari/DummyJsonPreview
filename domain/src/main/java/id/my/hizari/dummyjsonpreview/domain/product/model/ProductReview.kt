package id.my.hizari.dummyjsonpreview.domain.product.model

/**
 * id.my.hizari.dummyjsonpreview.domain.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ProductReview(
    val rating: Int,
    val comment: String?,
    val date: String?,
    val reviewerName: String?,
    val reviewerEmail: String?
)

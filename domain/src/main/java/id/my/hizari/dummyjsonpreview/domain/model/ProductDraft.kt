package id.my.hizari.dummyjsonpreview.domain.model

/**
 * id.my.hizari.dummyjsonpreview.domain.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Create and update payload. Every field is optional so the same type can carry a partial update.
 */
data class ProductDraft(
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val price: Double? = null,
    val discountPercentage: Double? = null,
    val stock: Int? = null,
    val brand: String? = null
)

data class DeletedProduct(
    val id: Int,
    val title: String?,
    val isDeleted: Boolean,
    val deletedOn: String?
)

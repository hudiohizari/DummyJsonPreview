package id.my.hizari.dummyjsonpreview.domain.product.model

/**
 * id.my.hizari.dummyjsonpreview.domain.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class Product(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String?,
    val price: Double,
    val discountPercentage: Double?,
    val rating: Double?,
    val stock: Int?,
    val tags: List<String> = emptyList(),
    val brand: String?,
    val sku: String?,
    val weight: Double?,
    val dimensions: Dimensions?,
    val warrantyInformation: String?,
    val shippingInformation: String?,
    val availabilityStatus: String?,
    val reviews: List<ProductReview> = emptyList(),
    val returnPolicy: String?,
    val minimumOrderQuantity: Int?,
    val meta: ProductMeta?,
    val thumbnail: String?,
    val images: List<String> = emptyList()
) {
    val discountedPrice: Double
        get() = discountPercentage?.let(block = { price * (1 - it / 100.0) }) ?: price
}

package id.my.hizari.dummyjsonpreview.data.product.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Every field is nullable on purpose. The add endpoint echoes back only the fields that were sent,
 * and Gson instantiates via Unsafe, so a non-null Kotlin property would silently hold null and
 * blow up somewhere far from the parse.
 */
data class ProductDto(
    @SerializedName(value = "id") val id: Int?,
    @SerializedName(value = "title") val title: String?,
    @SerializedName(value = "description") val description: String?,
    @SerializedName(value = "category") val category: String?,
    @SerializedName(value = "price") val price: Double?,
    @SerializedName(value = "discountPercentage") val discountPercentage: Double?,
    @SerializedName(value = "rating") val rating: Double?,
    @SerializedName(value = "stock") val stock: Int?,
    @SerializedName(value = "tags") val tags: List<String>?,
    @SerializedName(value = "brand") val brand: String?,
    @SerializedName(value = "sku") val sku: String?,
    @SerializedName(value = "weight") val weight: Double?,
    @SerializedName(value = "dimensions") val dimensions: DimensionsDto?,
    @SerializedName(value = "warrantyInformation") val warrantyInformation: String?,
    @SerializedName(value = "shippingInformation") val shippingInformation: String?,
    @SerializedName(value = "availabilityStatus") val availabilityStatus: String?,
    @SerializedName(value = "reviews") val reviews: List<ReviewDto>?,
    @SerializedName(value = "returnPolicy") val returnPolicy: String?,
    @SerializedName(value = "minimumOrderQuantity") val minimumOrderQuantity: Int?,
    @SerializedName(value = "meta") val meta: MetaDto?,
    @SerializedName(value = "thumbnail") val thumbnail: String?,
    @SerializedName(value = "images") val images: List<String>?
)

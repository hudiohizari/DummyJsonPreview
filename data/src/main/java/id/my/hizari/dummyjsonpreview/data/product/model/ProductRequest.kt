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
 * Nulls are omitted by Gson, so this doubles as a partial-update body.
 */
data class ProductRequest(
    @SerializedName(value = "title") val title: String? = null,
    @SerializedName(value = "description") val description: String? = null,
    @SerializedName(value = "category") val category: String? = null,
    @SerializedName(value = "price") val price: Double? = null,
    @SerializedName(value = "discountPercentage") val discountPercentage: Double? = null,
    @SerializedName(value = "stock") val stock: Int? = null,
    @SerializedName(value = "brand") val brand: String? = null
)

package id.my.hizari.dummyjsonpreview.data.product.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ProductListResponse(
    @SerializedName(value = "products") val products: List<ProductDto>?,
    @SerializedName(value = "total") val total: Int?,
    @SerializedName(value = "skip") val skip: Int?,
    @SerializedName(value = "limit") val limit: Int?
)

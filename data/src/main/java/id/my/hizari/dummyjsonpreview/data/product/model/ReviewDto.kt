package id.my.hizari.dummyjsonpreview.data.product.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ReviewDto(
    @SerializedName(value = "rating") val rating: Int?,
    @SerializedName(value = "comment") val comment: String?,
    @SerializedName(value = "date") val date: String?,
    @SerializedName(value = "reviewerName") val reviewerName: String?,
    @SerializedName(value = "reviewerEmail") val reviewerEmail: String?
)

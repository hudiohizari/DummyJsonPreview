package id.my.hizari.dummyjsonpreview.data.product.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class DeleteProductResponse(
    @SerializedName(value = "id") val id: Int?,
    @SerializedName(value = "title") val title: String?,
    @SerializedName(value = "isDeleted") val isDeleted: Boolean?,
    @SerializedName(value = "deletedOn") val deletedOn: String?
)

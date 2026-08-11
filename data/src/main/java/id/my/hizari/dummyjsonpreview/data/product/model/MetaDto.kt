package id.my.hizari.dummyjsonpreview.data.product.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class MetaDto(
    @SerializedName(value = "createdAt") val createdAt: String?,
    @SerializedName(value = "updatedAt") val updatedAt: String?,
    @SerializedName(value = "barcode") val barcode: String?,
    @SerializedName(value = "qrCode") val qrCode: String?
)

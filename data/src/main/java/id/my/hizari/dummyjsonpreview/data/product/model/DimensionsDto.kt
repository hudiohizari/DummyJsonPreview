package id.my.hizari.dummyjsonpreview.data.product.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class DimensionsDto(
    @SerializedName(value = "width") val width: Double?,
    @SerializedName(value = "height") val height: Double?,
    @SerializedName(value = "depth") val depth: Double?
)

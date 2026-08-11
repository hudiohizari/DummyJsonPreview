package id.my.hizari.dummyjsonpreview.data.network

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.network
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Every DummyJSON failure body has this shape, whatever the status code.
 */
data class ErrorResponse(
    @SerializedName(value = "message") val message: String?
)

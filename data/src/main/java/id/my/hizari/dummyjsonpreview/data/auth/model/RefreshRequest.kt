package id.my.hizari.dummyjsonpreview.data.auth.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.auth.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class RefreshRequest(
    @SerializedName(value = "refreshToken") val refreshToken: String,
    @SerializedName(value = "expiresInMins") val expiresInMins: Int? = null
)

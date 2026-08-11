package id.my.hizari.dummyjsonpreview.data.auth.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.auth.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class LoginRequest(
    @SerializedName(value = "username") val username: String,
    @SerializedName(value = "password") val password: String,
    @SerializedName(value = "expiresInMins") val expiresInMins: Int? = null
)

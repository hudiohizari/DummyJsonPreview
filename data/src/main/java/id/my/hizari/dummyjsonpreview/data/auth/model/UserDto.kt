package id.my.hizari.dummyjsonpreview.data.auth.model

import com.google.gson.annotations.SerializedName

/**
 * id.my.hizari.dummyjsonpreview.data.auth.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * /auth/me returns far more fields than this; Gson ignores the ones we do not declare.
 */
data class UserDto(
    @SerializedName(value = "id") val id: Int?,
    @SerializedName(value = "username") val username: String?,
    @SerializedName(value = "email") val email: String?,
    @SerializedName(value = "firstName") val firstName: String?,
    @SerializedName(value = "lastName") val lastName: String?,
    @SerializedName(value = "gender") val gender: String?,
    @SerializedName(value = "image") val image: String?
)

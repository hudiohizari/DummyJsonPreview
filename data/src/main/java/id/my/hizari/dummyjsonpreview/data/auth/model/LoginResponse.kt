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
 * The login endpoint returns the user fields flat, alongside the two tokens.
 */
data class LoginResponse(
    @SerializedName(value = "id") val id: Int?,
    @SerializedName(value = "username") val username: String?,
    @SerializedName(value = "email") val email: String?,
    @SerializedName(value = "firstName") val firstName: String?,
    @SerializedName(value = "lastName") val lastName: String?,
    @SerializedName(value = "gender") val gender: String?,
    @SerializedName(value = "image") val image: String?,
    @SerializedName(value = "accessToken") val accessToken: String?,
    @SerializedName(value = "refreshToken") val refreshToken: String?
)

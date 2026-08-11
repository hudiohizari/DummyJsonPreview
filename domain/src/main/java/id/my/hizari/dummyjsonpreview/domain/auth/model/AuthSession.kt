package id.my.hizari.dummyjsonpreview.domain.auth.model

/**
 * id.my.hizari.dummyjsonpreview.domain.auth.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

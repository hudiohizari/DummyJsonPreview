package id.my.hizari.dummyjsonpreview.domain.model

/**
 * id.my.hizari.dummyjsonpreview.domain.model
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

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)

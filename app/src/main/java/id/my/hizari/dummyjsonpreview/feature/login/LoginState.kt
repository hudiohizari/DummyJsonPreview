package id.my.hizari.dummyjsonpreview.feature.login

/**
 * id.my.hizari.dummyjsonpreview.feature.login
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isUsernameBlank: Boolean = false,
    val isPasswordBlank: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

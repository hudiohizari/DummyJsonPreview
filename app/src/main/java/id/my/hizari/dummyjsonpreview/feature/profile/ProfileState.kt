package id.my.hizari.dummyjsonpreview.feature.profile

import id.my.hizari.dummyjsonpreview.domain.auth.model.User

/**
 * id.my.hizari.dummyjsonpreview.feature.profile
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class ProfileState(
    val user: User? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isLoggingOut: Boolean = false
)

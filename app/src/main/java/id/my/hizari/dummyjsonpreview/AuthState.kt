package id.my.hizari.dummyjsonpreview

/**
 * id.my.hizari.dummyjsonpreview
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Loading exists so the splash can be held until the stored session has been read, which is what
 * stops the login screen flashing on a cold start.
 */
sealed interface AuthState {

    data object Loading : AuthState

    data object Authenticated : AuthState

    data object Unauthenticated : AuthState
}

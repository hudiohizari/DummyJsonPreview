package id.my.hizari.dummyjsonpreview.domain.auth.repository

import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

/**
 * id.my.hizari.dummyjsonpreview.domain.auth.repository
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

interface AuthRepository {

    suspend fun login(username: String, password: String): User

    suspend fun refreshProfile(): User

    /** Emits null as soon as the session is cleared, which is what drives the logout redirect. */
    fun observeCurrentUser(): Flow<User?>

    suspend fun hasActiveSession(): Boolean

    suspend fun logout()
}

package id.my.hizari.dummyjsonpreview.data.auth.repository

import id.my.hizari.dummyjsonpreview.data.auth.api.AuthApi
import id.my.hizari.dummyjsonpreview.data.network.DummyJsonConfig
import id.my.hizari.dummyjsonpreview.data.auth.api.UserApi
import id.my.hizari.dummyjsonpreview.data.auth.mapper.toDomain
import id.my.hizari.dummyjsonpreview.data.auth.model.LoginRequest
import id.my.hizari.dummyjsonpreview.data.auth.session.SessionManager
import id.my.hizari.dummyjsonpreview.data.network.ApiErrorMapper
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.auth.repository
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val sessionManager: SessionManager,
    private val errorMapper: ApiErrorMapper
) : AuthRepository {

    override suspend fun login(username: String, password: String): User = errorMapper.call(block = {
        val session = authApi.login(
            request = LoginRequest(
                username = username,
                password = password,
                expiresInMins = DummyJsonConfig.TOKEN_LIFETIME_MINUTES
            )
        ).toDomain()

        sessionManager.saveSession(session = session)
        session.user
    })

    override suspend fun refreshProfile(): User = errorMapper.call(block = {
        val user = userApi.me().toDomain()
        sessionManager.saveUser(user = user)
        user
    })

    override fun observeCurrentUser(): Flow<User?> = sessionManager.currentUser

    override suspend fun hasActiveSession(): Boolean = sessionManager.hasActiveSession()

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}

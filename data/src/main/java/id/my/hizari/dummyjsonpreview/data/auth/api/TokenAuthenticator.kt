package id.my.hizari.dummyjsonpreview.data.auth.api

import id.my.hizari.dummyjsonpreview.data.auth.mapper.toDomain
import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Runs when the server answers 401. Returning a request replays it; returning null lets the 401
 * reach Retrofit, where it becomes an Unauthorized failure.
 *
 * The api here comes from the unauthenticated client, so refreshing can never recurse back into
 * this authenticator.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // No token on the failed request means there is nothing to refresh.
        val failedAuthHeader = response.request.header(name = AuthInterceptor.HEADER_AUTHORIZATION)
            ?: return null

        if (responseCount(response = response) >= MAX_ATTEMPTS) {
            tokenStore.clear()
            return null
        }

        synchronized(lock = this, block = {
            val failedToken = failedAuthHeader.removePrefix(prefix = AuthInterceptor.BEARER_PREFIX)
            val storedToken = tokenStore.currentAccessToken()

            // Another request refreshed while this one waited on the lock.
            if (!storedToken.isNullOrBlank() && storedToken != failedToken) {
                return response.request.retryWith(token = storedToken)
            }

            val refreshToken = tokenStore.currentRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                tokenStore.clear()
                return null
            }

            return try {
                val refreshed = authApi.refreshSync(request = RefreshRequest(refreshToken = refreshToken))
                    .execute()
                val body = refreshed.body()
                if (!refreshed.isSuccessful || body == null) {
                    tokenStore.clear()
                    null
                } else {
                    val tokens = body.toDomain()
                    tokenStore.saveTokens(tokens = tokens)
                    response.request.retryWith(token = tokens.accessToken)
                }
            } catch (throwable: Throwable) {
                tokenStore.clear()
                null
            }
        })
    }

    private fun Request.retryWith(token: String): Request = newBuilder()
        .header(name = AuthInterceptor.HEADER_AUTHORIZATION, value = "${AuthInterceptor.BEARER_PREFIX}$token")
        .build()

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
    }
}

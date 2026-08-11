package id.my.hizari.dummyjsonpreview.data.auth.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenStore.currentAccessToken()
        if (token.isNullOrBlank()) return chain.proceed(request = request)

        return chain.proceed(
            request = request.newBuilder()
                .header(name = HEADER_AUTHORIZATION, value = "$BEARER_PREFIX$token")
                .build()
        )
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}

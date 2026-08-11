package id.my.hizari.dummyjsonpreview.data.auth.api

import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthTokens

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Blocking view of the stored session, for the OkHttp interceptor and authenticator.
 *
 * Callers must be on an OkHttp dispatcher thread. Keeping this an interface means the interceptor
 * depends on an abstraction rather than on DataStore, so its tests need no Android machinery.
 */
interface TokenStore {

    fun currentAccessToken(): String?

    fun currentRefreshToken(): String?

    fun saveTokens(tokens: AuthTokens)

    fun clear()
}

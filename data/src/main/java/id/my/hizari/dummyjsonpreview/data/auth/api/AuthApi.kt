package id.my.hizari.dummyjsonpreview.data.auth.api

import id.my.hizari.dummyjsonpreview.data.auth.model.LoginRequest
import id.my.hizari.dummyjsonpreview.data.auth.model.LoginResponse
import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshRequest
import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Served by the unauthenticated client, so a login can never carry a stale token.
 */
interface AuthApi {

    @POST(value = "auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST(value = "auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse

    /** Blocking variant for the authenticator, which runs on a thread that must not suspend. */
    @POST(value = "auth/refresh")
    fun refreshSync(@Body request: RefreshRequest): Call<RefreshResponse>
}

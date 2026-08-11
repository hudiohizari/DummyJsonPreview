package id.my.hizari.dummyjsonpreview.data.auth.api

import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshRequest
import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshResponse
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthTokens
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Call

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class TokenAuthenticatorTest {

    private val tokenStore: TokenStore = mockk(relaxed = true)
    private val authApi: AuthApi = mockk()
    private val authenticator = TokenAuthenticator(tokenStore = tokenStore, authApi = authApi)

    @Test
    fun `it gives up when the failed request never carried a token`() {
        val response = unauthorizedResponse(withAuthHeader = false)

        val retry = authenticator.authenticate(route = null, response = response)

        assertNull(retry)
        verify(exactly = 0, verifyBlock = { authApi.refreshSync(any()) })
    }

    @Test
    fun `it replays the request with a refreshed token`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "expired"
        every(stubBlock = { tokenStore.currentRefreshToken() }) returns "refresh-token"
        every(stubBlock = { authApi.refreshSync(any()) }) returns callReturning(
            response = retrofit2.Response.success(RefreshResponse(accessToken = "fresh-access", refreshToken = "fresh-refresh"))
        )

        val retry = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertNotNull(retry)
        assertEquals("Bearer fresh-access", retry!!.header(name = "Authorization"))
    }

    @Test
    fun `it persists the refreshed tokens`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "expired"
        every(stubBlock = { tokenStore.currentRefreshToken() }) returns "refresh-token"
        every(stubBlock = { authApi.refreshSync(any()) }) returns callReturning(
            response = retrofit2.Response.success(RefreshResponse(accessToken = "fresh-access", refreshToken = "fresh-refresh"))
        )
        val saved = slot<AuthTokens>()

        authenticator.authenticate(route = null, response = unauthorizedResponse())

        verify(exactly = 1, verifyBlock = { tokenStore.saveTokens(capture(lst = saved)) })
        assertEquals("fresh-access", saved.captured.accessToken)
        assertEquals("fresh-refresh", saved.captured.refreshToken)
    }

    @Test
    fun `it sends the stored refresh token to the endpoint`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "expired"
        every(stubBlock = { tokenStore.currentRefreshToken() }) returns "refresh-token"
        val request = slot<RefreshRequest>()
        every(stubBlock = { authApi.refreshSync(capture(lst = request)) }) returns callReturning(
            response = retrofit2.Response.success(RefreshResponse(accessToken = "fresh-access", refreshToken = "fresh-refresh"))
        )

        authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertEquals("refresh-token", request.captured.refreshToken)
    }

    @Test
    fun `it clears the session when there is no refresh token`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "expired"
        every(stubBlock = { tokenStore.currentRefreshToken() }) returns null

        val retry = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertNull(retry)
        verify(exactly = 1, verifyBlock = { tokenStore.clear() })
        verify(exactly = 0, verifyBlock = { authApi.refreshSync(any()) })
    }

    @Test
    fun `it clears the session when the refresh itself is rejected`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "expired"
        every(stubBlock = { tokenStore.currentRefreshToken() }) returns "refresh-token"
        every(stubBlock = { authApi.refreshSync(any()) }) returns callReturning(
            response = retrofit2.Response.error(
                401,
                """{"message":"Invalid/Expired Token!"}""".toResponseBody("application/json".toMediaType())
            )
        )

        val retry = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertNull(retry)
        verify(exactly = 1, verifyBlock = { tokenStore.clear() })
    }

    @Test
    fun `it clears the session when the refresh call throws`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "expired"
        every(stubBlock = { tokenStore.currentRefreshToken() }) returns "refresh-token"
        val call: Call<RefreshResponse> = mockk()
        every(stubBlock = { call.execute() }) throws java.io.IOException("offline")
        every(stubBlock = { authApi.refreshSync(any()) }) returns call

        val retry = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertNull(retry)
        verify(exactly = 1, verifyBlock = { tokenStore.clear() })
    }

    /**
     * Without this guard a server that keeps answering 401 would have the authenticator refreshing
     * forever instead of surfacing the failure.
     */
    @Test
    fun `it stops after a retry has already been attempted`() {
        val response = unauthorizedResponse(priorAttempts = 2)

        val retry = authenticator.authenticate(route = null, response = response)

        assertNull(retry)
        verify(exactly = 0, verifyBlock = { authApi.refreshSync(any()) })
    }

    /**
     * Another in-flight request may have refreshed while this one waited on the lock, in which case
     * replaying with the newly stored token is enough.
     */
    @Test
    fun `it reuses a token refreshed by another request instead of refreshing again`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "already-refreshed"

        val retry = authenticator.authenticate(route = null, response = unauthorizedResponse())

        assertEquals("Bearer already-refreshed", retry!!.header(name = "Authorization"))
        verify(exactly = 0, verifyBlock = { authApi.refreshSync(any()) })
    }

    private fun callReturning(response: retrofit2.Response<RefreshResponse>): Call<RefreshResponse> {
        val call: Call<RefreshResponse> = mockk()
        every(stubBlock = { call.execute() }) returns response
        return call
    }

    private fun unauthorizedResponse(
        withAuthHeader: Boolean = true,
        priorAttempts: Int = 1
    ): Response {
        val requestBuilder = Request.Builder().url(url = "https://dummyjson.com/auth/me")
        if (withAuthHeader) {
            requestBuilder.header(name = "Authorization", value = "Bearer expired")
        }
        val request = requestBuilder.build()

        var response = buildResponse(request = request)
        repeat(times = priorAttempts - 1, action = {
            response = buildResponse(request = request).newBuilder().priorResponse(priorResponse = response).build()
        })
        return response
    }

    private fun buildResponse(request: Request): Response = Response.Builder()
        .request(request = request)
        .protocol(protocol = Protocol.HTTP_1_1)
        .code(code = 401)
        .message(message = "Unauthorized")
        .build()
}

package id.my.hizari.dummyjsonpreview.data.auth.api

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class AuthInterceptorTest {

    private val tokenStore: TokenStore = mockk()
    private val interceptor = AuthInterceptor(tokenStore = tokenStore)

    @Test
    fun `it attaches a bearer header when a token is stored`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "abc123"
        val sent = slot<Request>()

        interceptor.intercept(chainCapturing(sent = sent))

        assertEquals("Bearer abc123", sent.captured.header(name = "Authorization"))
    }

    @Test
    fun `it sends no authorization header when there is no token`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns null
        val sent = slot<Request>()

        interceptor.intercept(chainCapturing(sent = sent))

        assertNull(sent.captured.header(name = "Authorization"))
    }

    @Test
    fun `it sends no authorization header when the token is blank`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "   "
        val sent = slot<Request>()

        interceptor.intercept(chainCapturing(sent = sent))

        assertNull(sent.captured.header(name = "Authorization"))
    }

    @Test
    fun `it leaves the rest of the request untouched`() {
        every(stubBlock = { tokenStore.currentAccessToken() }) returns "abc123"
        val sent = slot<Request>()

        interceptor.intercept(chainCapturing(sent = sent))

        assertEquals("https://dummyjson.com/products", sent.captured.url.toString())
        assertEquals("GET", sent.captured.method)
    }

    private fun chainCapturing(sent: CapturingSlot<Request>): Interceptor.Chain {
        val original = Request.Builder().url(url = "https://dummyjson.com/products").build()
        val chain: Interceptor.Chain = mockk()
        every(stubBlock = { chain.request() }) returns original
        every(stubBlock = { chain.proceed(capture(lst = sent)) }).answers(answer = {
            Response.Builder()
                .request(request = firstArg())
                .protocol(protocol = Protocol.HTTP_1_1)
                .code(code = 200)
                .message(message = "OK")
                .build()
        })
        return chain
    }
}

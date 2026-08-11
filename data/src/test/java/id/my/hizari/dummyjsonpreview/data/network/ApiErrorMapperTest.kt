package id.my.hizari.dummyjsonpreview.data.network

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * id.my.hizari.dummyjsonpreview.data.network
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ApiErrorMapperTest {

    private val mapper = ApiErrorMapper(gson = Gson())

    @Test
    fun `an unknown host becomes a network failure`() {
        assertTrue(mapper.map(throwable = UnknownHostException()) is AppException.Network)
    }

    @Test
    fun `a refused connection becomes a network failure`() {
        assertTrue(mapper.map(throwable = ConnectException()) is AppException.Network)
    }

    @Test
    fun `a socket timeout becomes a timeout failure`() {
        assertTrue(mapper.map(throwable = SocketTimeoutException()) is AppException.Timeout)
    }

    @Test
    fun `a generic io failure becomes a network failure`() {
        assertTrue(mapper.map(throwable = IOException()) is AppException.Network)
    }

    @Test
    fun `a 401 becomes unauthorized and keeps the server message`() {
        val mapped = mapper.map(throwable = httpException(code = 401, body = """{"message":"Invalid/Expired Token!"}"""))

        assertTrue(mapped is AppException.Unauthorized)
        assertEquals("Invalid/Expired Token!", (mapped as AppException.Unauthorized).serverMessage)
    }

    @Test
    fun `a 400 keeps both the code and the server message`() {
        val mapped = mapper.map(throwable = httpException(code = 400, body = """{"message":"Invalid credentials"}"""))

        assertTrue(mapped is AppException.Http)
        mapped as AppException.Http
        assertEquals(400, mapped.code)
        assertEquals("Invalid credentials", mapped.serverMessage)
    }

    @Test
    fun `a 404 keeps both the code and the server message`() {
        val mapped = mapper.map(throwable = 
            httpException(code = 404, body = """{"message":"Product with id '99999' not found"}""")
        )

        mapped as AppException.Http
        assertEquals(404, mapped.code)
        assertEquals("Product with id '99999' not found", mapped.serverMessage)
    }

    @Test
    fun `an unparseable error body still yields the status code`() {
        val mapped = mapper.map(throwable = httpException(code = 500, body = "<html>server exploded</html>"))

        mapped as AppException.Http
        assertEquals(500, mapped.code)
    }

    @Test
    fun `anything else becomes unknown`() {
        assertTrue(mapper.map(throwable = IllegalStateException("boom")) is AppException.Unknown)
    }

    @Test
    fun `an already mapped failure passes through unchanged`() {
        val original = AppException.Unauthorized(serverMessage = "nope")

        assertEquals(original, mapper.map(throwable = original))
    }

    @Test
    fun `call wraps a thrown failure`() = runTest(testBody = {
        val thrown = assertThrows(AppException.Network::class.java) {
            kotlinx.coroutines.runBlocking(block = {
                mapper.call(block = { throw UnknownHostException() })
            })
        }

        assertTrue(thrown is AppException.Network)
    })

    @Test
    fun `call returns the value when nothing goes wrong`() = runTest(testBody = {
        assertEquals("ok", mapper.call(block = { "ok" }))
    })

    /**
     * Swallowing cancellation would break structured concurrency, so it must never be translated
     * into a domain failure.
     */
    @Test
    fun `call rethrows cancellation untouched`() = runTest(testBody = {
        assertThrows(CancellationException::class.java) {
            kotlinx.coroutines.runBlocking(block = {
                mapper.call(block = { throw CancellationException("cancelled") })
            })
        }
    })

    private fun httpException(code: Int, body: String): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, responseBody))
    }
}

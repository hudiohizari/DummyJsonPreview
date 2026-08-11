package id.my.hizari.dummyjsonpreview.data.auth.repository

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.data.auth.api.AuthApi
import id.my.hizari.dummyjsonpreview.data.network.DummyJsonConfig
import id.my.hizari.dummyjsonpreview.data.auth.api.UserApi
import id.my.hizari.dummyjsonpreview.data.auth.model.LoginRequest
import id.my.hizari.dummyjsonpreview.data.auth.model.LoginResponse
import id.my.hizari.dummyjsonpreview.data.auth.model.UserDto
import id.my.hizari.dummyjsonpreview.data.auth.session.SessionManager
import id.my.hizari.dummyjsonpreview.data.network.ApiErrorMapper
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthSession
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

/**
 * id.my.hizari.dummyjsonpreview.data.auth.repository
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class AuthRepositoryImplTest {

    private val authApi: AuthApi = mockk()
    private val userApi: UserApi = mockk()
    private val sessionManager: SessionManager = mockk(relaxed = true)

    // The real mapper is used deliberately, so the test covers the translation too.
    private val repository = AuthRepositoryImpl(
        authApi = authApi,
        userApi = userApi,
        sessionManager = sessionManager,
        errorMapper = ApiErrorMapper(gson = Gson())
    )

    @Test
    fun `a successful login persists the session and returns the user`() = runTest(testBody = {
        coEvery(stubBlock = { authApi.login(request = any()) }) returns loginResponse()
        val saved = slot<AuthSession>()

        val user = repository.login(username = "emilys", password = "emilyspass")

        coVerify(exactly = 1, verifyBlock = { sessionManager.saveSession(capture(lst = saved)) })
        assertEquals("access-token-value", saved.captured.accessToken)
        assertEquals("refresh-token-value", saved.captured.refreshToken)
        assertEquals("Emily Johnson", user.fullName)
    })

    @Test
    fun `login asks for a long lived token so the session survives a restart`() = runTest(testBody = {
        coEvery(stubBlock = { authApi.login(request = any()) }) returns loginResponse()
        val request = slot<LoginRequest>()

        repository.login(username = "emilys", password = "emilyspass")

        coVerify(verifyBlock = { authApi.login(capture(lst = request)) })
        assertEquals("emilys", request.captured.username)
        assertEquals("emilyspass", request.captured.password)
        assertEquals(DummyJsonConfig.TOKEN_LIFETIME_MINUTES, request.captured.expiresInMins)
    })

    @Test
    fun `bad credentials surface as a http failure carrying the server message`() = runTest(testBody = {
        coEvery(stubBlock = { authApi.login(request = any()) }) throws httpException(code = 400, body = """{"message":"Invalid credentials"}""")

        val thrown = assertThrows(AppException.Http::class.java) {
            kotlinx.coroutines.runBlocking(block = { repository.login(username = "emilys", password = "wrong") })
        }

        assertEquals(400, thrown.code)
        assertEquals("Invalid credentials", thrown.serverMessage)
    })

    @Test
    fun `being offline surfaces as a network failure`() = runTest(testBody = {
        coEvery(stubBlock = { authApi.login(request = any()) }) throws UnknownHostException()

        assertThrows(AppException.Network::class.java) {
            kotlinx.coroutines.runBlocking(block = { repository.login(username = "emilys", password = "emilyspass") })
        }
    })

    @Test
    fun `a failed login stores nothing`() = runTest(testBody = {
        coEvery(stubBlock = { authApi.login(request = any()) }) throws UnknownHostException()

        runCatching(block = { repository.login(username = "emilys", password = "emilyspass") })

        coVerify(exactly = 0, verifyBlock = { sessionManager.saveSession(session = any()) })
    })

    @Test
    fun `refreshing the profile caches the newer copy`() = runTest(testBody = {
        coEvery(stubBlock = { userApi.me() }) returns UserDto(
            id = 1,
            username = "emilys",
            email = "emily.johnson@x.dummyjson.com",
            firstName = "Emily",
            lastName = "Johnson",
            gender = "female",
            image = null
        )
        val saved = slot<User>()

        val user = repository.refreshProfile()

        coVerify(exactly = 1, verifyBlock = { sessionManager.saveUser(capture(lst = saved)) })
        assertEquals("Emily Johnson", saved.captured.fullName)
        assertEquals("Emily Johnson", user.fullName)
    })

    @Test
    fun `logout clears the stored session`() = runTest(testBody = {
        repository.logout()

        coVerify(exactly = 1, verifyBlock = { sessionManager.clearSession() })
    })

    @Test
    fun `it reports whether a session is active`() = runTest(testBody = {
        coEvery(stubBlock = { sessionManager.hasActiveSession() }) returns true

        assertTrue(repository.hasActiveSession())
    })

    @Test
    fun `it exposes the stored user stream`() = runTest(testBody = {
        every(stubBlock = { sessionManager.currentUser }) returns flowOf(null)

        assertNull(repository.observeCurrentUser().first())
    })

    private fun loginResponse() = LoginResponse(
        id = 1,
        username = "emilys",
        email = "emily.johnson@x.dummyjson.com",
        firstName = "Emily",
        lastName = "Johnson",
        gender = "female",
        image = "https://dummyjson.com/icon/emilys/128",
        accessToken = "access-token-value",
        refreshToken = "refresh-token-value"
    )

    private fun httpException(code: Int, body: String): HttpException =
        HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaType())))
}

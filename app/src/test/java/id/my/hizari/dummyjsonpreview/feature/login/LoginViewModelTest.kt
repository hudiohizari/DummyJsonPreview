package id.my.hizari.dummyjsonpreview.feature.login

import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LoginUseCase
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

/**
 * id.my.hizari.dummyjsonpreview.feature.login
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mockk()
    private val viewModel = LoginViewModel(loginUseCase = loginUseCase)

    @Test
    fun `it forwards the credentials as entered`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(any(), any()) }) returns user()

        viewModel.signIn(username = "emilys", password = "emilyspass")
        advanceUntilIdle()

        coVerify(
            exactly = 1,
            verifyBlock = { loginUseCase(username = "emilys", password = "emilyspass") }
        )
    })

    @Test
    fun `it clears loading once the call resolves`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(any(), any()) }) returns user()

        viewModel.signIn(username = "emilys", password = "emilyspass")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    })

    /** The server message is the useful part of a rejected login, so it must reach the state. */
    @Test
    fun `bad credentials surface the server message`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(any(), any()) }) throws AppException.Http(
            code = 400,
            serverMessage = "Invalid credentials"
        )

        viewModel.signIn(username = "emilys", password = "wrong")
        advanceUntilIdle()

        assertEquals("Invalid credentials", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    })

    @Test
    fun `being offline surfaces a message rather than crashing`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(any(), any()) }) throws AppException.Network(
            cause = UnknownHostException()
        )

        viewModel.signIn(username = "emilys", password = "emilyspass")
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.errorMessage)
    })

    private fun user() = User(
        id = 1,
        username = "emilys",
        email = "emily.johnson@x.dummyjson.com",
        firstName = "Emily",
        lastName = "Johnson",
        gender = "female",
        image = null
    )
}

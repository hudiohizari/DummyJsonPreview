package id.my.hizari.dummyjsonpreview.feature.login

import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LoginUseCase
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mockk()
    private val viewModel = LoginViewModel(loginUseCase = loginUseCase)

    @Test
    fun `it prefills the demo credentials so the app can be signed into immediately`() {
        assertEquals("emilys", viewModel.state.value.username)
        assertEquals("emilyspass", viewModel.state.value.password)
    }

    @Test
    fun `typing updates the username`() {
        viewModel.onUsernameChange(username = "someone")

        assertEquals("someone", viewModel.state.value.username)
    }

    @Test
    fun `typing updates the password`() {
        viewModel.onPasswordChange(password = "secret")

        assertEquals("secret", viewModel.state.value.password)
    }

    @Test
    fun `a blank username is rejected without calling the api`() = runTest(testBody = {
        viewModel.onUsernameChange(username = "")

        viewModel.signIn()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isUsernameBlank)
        coVerify(exactly = 0, verifyBlock = { loginUseCase(username = any(), password = any()) })
    })

    @Test
    fun `a whitespace only username counts as blank`() = runTest(testBody = {
        viewModel.onUsernameChange(username = "   ")

        viewModel.signIn()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isUsernameBlank)
        coVerify(exactly = 0, verifyBlock = { loginUseCase(username = any(), password = any()) })
    })

    @Test
    fun `a blank password is rejected without calling the api`() = runTest(testBody = {
        viewModel.onPasswordChange(password = "")

        viewModel.signIn()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isPasswordBlank)
        coVerify(exactly = 0, verifyBlock = { loginUseCase(username = any(), password = any()) })
    })

    /** The field error has to disappear as soon as the user starts fixing it. */
    @Test
    fun `editing a rejected field clears its error`() = runTest(testBody = {
        viewModel.onUsernameChange(username = "")
        viewModel.signIn()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isUsernameBlank)

        viewModel.onUsernameChange(username = "e")

        assertFalse(viewModel.state.value.isUsernameBlank)
    })

    @Test
    fun `it submits whatever is currently in the fields`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(username = any(), password = any()) }) returns user()
        viewModel.onUsernameChange(username = "someone")
        viewModel.onPasswordChange(password = "secret")

        viewModel.signIn()
        advanceUntilIdle()

        coVerify(
            exactly = 1,
            verifyBlock = { loginUseCase(username = "someone", password = "secret") }
        )
    })

    @Test
    fun `it shows loading while the call is in flight and clears it afterwards`() = runTest(
        testBody = {
            coEvery(stubBlock = { loginUseCase(username = any(), password = any()) }) returns user()

            viewModel.signIn()
            // The coroutine has not been allowed to run yet, so this is the in-flight state.
            assertTrue(viewModel.state.value.isLoading)

            advanceUntilIdle()

            assertFalse(viewModel.state.value.isLoading)
            assertNull(viewModel.state.value.errorMessage)
            coVerify(exactly = 1, verifyBlock = { loginUseCase(username = any(), password = any()) })
        }
    )

    @Test
    fun `bad credentials surface the server message`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(username = any(), password = any()) }) throws AppException.Http(
            code = 400,
            serverMessage = "Invalid credentials"
        )

        viewModel.signIn()
        advanceUntilIdle()

        assertEquals("Invalid credentials", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    })

    @Test
    fun `being offline surfaces a message rather than crashing`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(username = any(), password = any()) }) throws AppException.Network(
            cause = UnknownHostException()
        )

        viewModel.signIn()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.errorMessage)
    })

    @Test
    fun `retrying clears the previous failure before calling again`() = runTest(testBody = {
        coEvery(stubBlock = { loginUseCase(username = any(), password = any()) }) throws AppException.Http(
            code = 400,
            serverMessage = "Invalid credentials"
        )
        viewModel.signIn()
        advanceUntilIdle()
        assertEquals("Invalid credentials", viewModel.state.value.errorMessage)

        coEvery(stubBlock = { loginUseCase(username = any(), password = any()) }) returns user()
        viewModel.signIn()
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
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

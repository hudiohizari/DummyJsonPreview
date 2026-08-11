package id.my.hizari.dummyjsonpreview

import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCurrentUser: ObserveCurrentUserUseCase = mockk()

    /**
     * The splash screen is held while this is Loading, so an incorrect initial value is what causes
     * the login screen to flash before a stored session is read.
     */
    @Test
    fun `it starts in loading so the gate can hold the splash`() = runTest(testBody = {
        every(stubBlock = { observeCurrentUser() }) returns MutableStateFlow(value = null)

        val viewModel = MainViewModel(observeCurrentUserUseCase = observeCurrentUser)

        assertEquals(AuthState.Loading, viewModel.authState.value)
    })

    @Test
    fun `a stored user resolves to authenticated`() = runTest(testBody = {
        every(stubBlock = { observeCurrentUser() }) returns flowOf(user())

        val viewModel = MainViewModel(observeCurrentUserUseCase = observeCurrentUser)
        advanceUntilIdle()

        assertEquals(AuthState.Authenticated, viewModel.authState.value)
    })

    @Test
    fun `no stored user resolves to unauthenticated`() = runTest(testBody = {
        every(stubBlock = { observeCurrentUser() }) returns flowOf(null)

        val viewModel = MainViewModel(observeCurrentUserUseCase = observeCurrentUser)
        advanceUntilIdle()

        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
    })

    /** Clearing the session is what drives the redirect back to login. */
    @Test
    fun `clearing the session flips authenticated back to unauthenticated`() = runTest(testBody = {
        val users = MutableStateFlow<User?>(value = user())
        every(stubBlock = { observeCurrentUser() }) returns users

        val viewModel = MainViewModel(observeCurrentUserUseCase = observeCurrentUser)
        advanceUntilIdle()
        assertEquals(AuthState.Authenticated, viewModel.authState.value)

        users.value = null
        advanceUntilIdle()

        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
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

package id.my.hizari.dummyjsonpreview.feature.profile

import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LogoutUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.feature.profile
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val logoutUseCase: LogoutUseCase = mockk()
    private val observeCurrentUser: ObserveCurrentUserUseCase = mockk()

    @Test
    fun `it exposes the signed in user`() = runTest(testBody = {
        every(stubBlock = { observeCurrentUser() }) returns flowOf(user())

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Emily Johnson", viewModel.user.value?.fullName)
        assertEquals("emily.johnson@x.dummyjson.com", viewModel.user.value?.email)
    })

    @Test
    fun `logout clears the session exactly once`() = runTest(testBody = {
        every(stubBlock = { observeCurrentUser() }) returns flowOf(user())
        coEvery(stubBlock = { logoutUseCase() }) returns Unit

        viewModel().logout()
        advanceUntilIdle()

        coVerify(exactly = 1, verifyBlock = { logoutUseCase() })
    })

    private fun viewModel() = ProfileViewModel(
        logoutUseCase = logoutUseCase,
        observeCurrentUserUseCase = observeCurrentUser
    )

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

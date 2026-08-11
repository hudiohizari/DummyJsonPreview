package id.my.hizari.dummyjsonpreview.feature.profile

import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LogoutUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.RefreshProfileUseCase
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.feature.profile
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val logoutUseCase: LogoutUseCase = mockk()
    private val refreshProfile: RefreshProfileUseCase = mockk()
    private val observeCurrentUser: ObserveCurrentUserUseCase = mockk()

    @Test
    fun `it exposes the signed in user`() = runTest(testBody = {
        stubUser()

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Emily Johnson", viewModel.state.value.user?.fullName)
        assertEquals("emily.johnson@x.dummyjson.com", viewModel.state.value.user?.email)
    })

    /** Signing out is destructive, so nothing may clear the session before the user confirms. */
    @Test
    fun `tapping log out only asks for confirmation`() = runTest(testBody = {
        stubUser()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLogoutClick()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isLogoutDialogVisible)
        coVerify(exactly = 0, verifyBlock = { logoutUseCase() })
    })

    @Test
    fun `dismissing the confirmation keeps the session`() = runTest(testBody = {
        stubUser()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLogoutClick()
        assertTrue(viewModel.state.value.isLogoutDialogVisible)

        viewModel.onLogoutDismiss()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLogoutDialogVisible)
        coVerify(exactly = 0, verifyBlock = { logoutUseCase() })
    })

    // Clearing the session is enough to move the user: the root graph follows the session state.
    @Test
    fun `confirming clears the session exactly once`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { logoutUseCase() }) returns Unit
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLogoutClick()
        viewModel.onLogoutConfirm()
        advanceUntilIdle()

        coVerify(exactly = 1, verifyBlock = { logoutUseCase() })
        assertFalse(viewModel.state.value.isLogoutDialogVisible)
    })

    @Test
    fun `confirming twice only signs out once`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { logoutUseCase() }) coAnswers { delay(timeMillis = 100) }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLogoutClick()
        viewModel.onLogoutConfirm()
        viewModel.onLogoutConfirm()
        advanceUntilIdle()

        coVerify(exactly = 1, verifyBlock = { logoutUseCase() })
    })

    @Test
    fun `refreshing fetches the profile again`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { refreshProfile() }) returns user()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRefreshing)
        assertNull(viewModel.state.value.errorMessage)
        coVerify(exactly = 1, verifyBlock = { refreshProfile() })
    })

    /** Guards against the flag being set and cleared without ever being observable. */
    @Test
    fun `it reports refreshing while the call is in flight`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { refreshProfile() }) coAnswers {
            delay(timeMillis = 100)
            user()
        }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceTimeBy(delayTimeMillis = 50)

        assertTrue(viewModel.state.value.isRefreshing)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRefreshing)
    })

    /** The stored user is still on screen, so a failed refresh must not blank the profile. */
    @Test
    fun `a failed refresh reports the error and keeps the user`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { refreshProfile() }) throws AppException.Network()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.errorMessage)
        assertEquals("Emily Johnson", viewModel.state.value.user?.fullName)
        assertFalse(viewModel.state.value.isRefreshing)
    })

    @Test
    fun `refreshing again clears the previous failure`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { refreshProfile() }) throws AppException.Network()
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.onRefresh()
        advanceUntilIdle()
        assertEquals("Network unavailable", viewModel.state.value.errorMessage)

        coEvery(stubBlock = { refreshProfile() }) returns user()
        viewModel.onRefresh()
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
    })

    private fun stubUser() {
        every(stubBlock = { observeCurrentUser() }) returns flowOf(user())
    }

    private fun viewModel() = ProfileViewModel(
        logoutUseCase = logoutUseCase,
        refreshProfileUseCase = refreshProfile,
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

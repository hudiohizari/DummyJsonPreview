package id.my.hizari.dummyjsonpreview.domain.auth.usecase

import id.my.hizari.dummyjsonpreview.domain.auth.repository.AuthRepository
import id.my.hizari.dummyjsonpreview.domain.stubUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.auth.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class SessionUseCaseTest {

    private val repository: AuthRepository = mockk()

    @Test
    fun `logout clears the session exactly once`() = runTest(testBody = {
        coEvery(stubBlock = { repository.logout() }) returns Unit

        LogoutUseCase(repository = repository).invoke()

        coVerify(exactly = 1, verifyBlock = { repository.logout() })
    })

    @Test
    fun `hasActiveSession reports true when a token is stored`() = runTest(testBody = {
        coEvery(stubBlock = { repository.hasActiveSession() }) returns true

        assertTrue(HasActiveSessionUseCase(repository = repository).invoke())
    })

    @Test
    fun `hasActiveSession reports false when no token is stored`() = runTest(testBody = {
        coEvery(stubBlock = { repository.hasActiveSession() }) returns false

        assertFalse(HasActiveSessionUseCase(repository = repository).invoke())
    })

    @Test
    fun `observeCurrentUser emits the stored user`() = runTest(testBody = {
        val expected = stubUser()
        every(stubBlock = { repository.observeCurrentUser() }) returns flowOf(expected)

        val actual = ObserveCurrentUserUseCase(repository = repository).invoke().first()

        assertEquals(expected, actual)
    })

    @Test
    fun `observeCurrentUser emits null once the session is cleared`() = runTest(testBody = {
        every(stubBlock = { repository.observeCurrentUser() }) returns flowOf(null)

        val actual = ObserveCurrentUserUseCase(repository = repository).invoke().first()

        assertNull(actual)
    })

    @Test
    fun `refreshProfile returns the refreshed user`() = runTest(testBody = {
        val expected = stubUser(firstName = "Refreshed")
        coEvery(stubBlock = { repository.refreshProfile() }) returns expected

        val actual = RefreshProfileUseCase(repository = repository).invoke()

        assertEquals(expected, actual)
        coVerify(exactly = 1, verifyBlock = { repository.refreshProfile() })
    })
}

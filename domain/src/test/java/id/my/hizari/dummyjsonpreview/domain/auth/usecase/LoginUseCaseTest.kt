package id.my.hizari.dummyjsonpreview.domain.auth.usecase

import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.auth.repository.AuthRepository
import id.my.hizari.dummyjsonpreview.domain.stubUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.auth.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class LoginUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val useCase = LoginUseCase(repository = repository)

    @Test
    fun `it trims the username so a stray space cannot fail the login`() = runTest(testBody = {
        coEvery(stubBlock = { repository.login(username = any(), password = any()) }) returns stubUser()

        useCase(username = "  emilys  ", password = "emilyspass")

        coVerify(exactly = 1, verifyBlock = { repository.login(username = "emilys", password = "emilyspass") })
    })

    @Test
    fun `it leaves the password untouched because whitespace can be significant`() = runTest(testBody = {
        coEvery(stubBlock = { repository.login(username = any(), password = any()) }) returns stubUser()

        useCase(username = "emilys", password = "  pass with spaces  ")

        coVerify(exactly = 1, verifyBlock = {
            repository.login(username = "emilys", password = "  pass with spaces  ")
        })
    })

    @Test
    fun `it returns the authenticated user`() = runTest(testBody = {
        val expected = stubUser()
        coEvery(stubBlock = { repository.login(username = any(), password = any()) }) returns expected

        val actual = useCase(username = "emilys", password = "emilyspass")

        assertEquals(expected, actual)
    })

    @Test
    fun `it lets repository failures propagate untouched`() = runTest(testBody = {
        coEvery(stubBlock = { repository.login(username = any(), password = any()) }) throws AppException.Http(
            code = 400,
            serverMessage = "Invalid credentials"
        )

        val thrown = assertThrows(AppException.Http::class.java) {
            kotlinx.coroutines.runBlocking(block = { useCase(username = "emilys", password = "wrong") })
        }

        assertEquals(400, thrown.code)
        assertEquals("Invalid credentials", thrown.serverMessage)
    })
}

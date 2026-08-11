package id.my.hizari.dummyjsonpreview.domain.usecase

import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.repository.AuthRepository
import id.my.hizari.dummyjsonpreview.domain.stubUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class LoginUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val useCase = LoginUseCase(repository = repository)

    @Test
    fun `it trims the username so a stray space cannot fail the login`() = runTest {
        coEvery { repository.login(any(), any()) } returns stubUser()

        useCase(username = "  emilys  ", password = "emilyspass")

        coVerify(exactly = 1) { repository.login(username = "emilys", password = "emilyspass") }
    }

    @Test
    fun `it leaves the password untouched because whitespace can be significant`() = runTest {
        coEvery { repository.login(any(), any()) } returns stubUser()

        useCase(username = "emilys", password = "  pass with spaces  ")

        coVerify(exactly = 1) {
            repository.login(username = "emilys", password = "  pass with spaces  ")
        }
    }

    @Test
    fun `it returns the authenticated user`() = runTest {
        val expected = stubUser()
        coEvery { repository.login(any(), any()) } returns expected

        val actual = useCase(username = "emilys", password = "emilyspass")

        assertEquals(expected, actual)
    }

    @Test
    fun `it lets repository failures propagate untouched`() = runTest {
        coEvery { repository.login(any(), any()) } throws AppException.Http(
            code = 400,
            serverMessage = "Invalid credentials"
        )

        val thrown = assertThrows(AppException.Http::class.java) {
            kotlinx.coroutines.runBlocking { useCase(username = "emilys", password = "wrong") }
        }

        assertEquals(400, thrown.code)
        assertEquals("Invalid credentials", thrown.serverMessage)
    }
}

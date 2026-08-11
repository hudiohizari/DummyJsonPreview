package id.my.hizari.dummyjsonpreview.domain.product.usecase

import id.my.hizari.dummyjsonpreview.domain.product.repository.ProductRepository
import id.my.hizari.dummyjsonpreview.domain.stubProductPage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.product.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class GetProductsUseCaseTest {

    private val repository: ProductRepository = mockk()
    private val useCase = GetProductsUseCase(repository = repository)

    @Test
    fun `a blank query lists products instead of searching`() = runTest(testBody = {
        coEvery(stubBlock = { repository.getProducts(any(), any()) }) returns stubProductPage()

        useCase(query = "", limit = 20, skip = 0)

        coVerify(exactly = 1, verifyBlock = { repository.getProducts(limit = 20, skip = 0) })
        coVerify(exactly = 0, verifyBlock = { repository.searchProducts(any(), any(), any()) })
    })

    @Test
    fun `a whitespace only query lists products instead of searching`() = runTest(testBody = {
        coEvery(stubBlock = { repository.getProducts(any(), any()) }) returns stubProductPage()

        useCase(query = "   ", limit = 20, skip = 0)

        coVerify(exactly = 1, verifyBlock = { repository.getProducts(limit = 20, skip = 0) })
        coVerify(exactly = 0, verifyBlock = { repository.searchProducts(any(), any(), any()) })
    })

    @Test
    fun `a non blank query searches instead of listing`() = runTest(testBody = {
        coEvery(stubBlock = { repository.searchProducts(any(), any(), any()) }) returns stubProductPage(count = 5, total = 5)

        useCase(query = "phone", limit = 20, skip = 0)

        coVerify(exactly = 1, verifyBlock = { repository.searchProducts(query = "phone", limit = 20, skip = 0) })
        coVerify(exactly = 0, verifyBlock = { repository.getProducts(any(), any()) })
    })

    @Test
    fun `the query is trimmed before it reaches the repository`() = runTest(testBody = {
        coEvery(stubBlock = { repository.searchProducts(any(), any(), any()) }) returns stubProductPage(count = 5, total = 5)

        useCase(query = "  phone  ", limit = 20, skip = 0)

        coVerify(exactly = 1, verifyBlock = { repository.searchProducts(query = "phone", limit = 20, skip = 0) })
    })

    @Test
    fun `paging arguments are forwarded unchanged when searching`() = runTest(testBody = {
        coEvery(stubBlock = { repository.searchProducts(any(), any(), any()) }) returns stubProductPage(count = 5, total = 23, skip = 20)

        useCase(query = "phone", limit = 20, skip = 20)

        coVerify(exactly = 1, verifyBlock = { repository.searchProducts(query = "phone", limit = 20, skip = 20) })
    })

    @Test
    fun `it returns the page the repository produced`() = runTest(testBody = {
        val expected = stubProductPage(count = 20, total = 194)
        coEvery(stubBlock = { repository.getProducts(any(), any()) }) returns expected

        val actual = useCase(query = "", limit = 20, skip = 0)

        assertEquals(expected, actual)
    })

    @Test
    fun `it defaults to the standard page size`() = runTest(testBody = {
        coEvery(stubBlock = { repository.getProducts(any(), any()) }) returns stubProductPage()

        useCase()

        coVerify(exactly = 1, verifyBlock = {
            repository.getProducts(limit = GetProductsUseCase.PAGE_SIZE, skip = 0)
        })
    })
}

package id.my.hizari.dummyjsonpreview.domain.product.usecase

import id.my.hizari.dummyjsonpreview.domain.product.model.DeletedProduct
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import id.my.hizari.dummyjsonpreview.domain.product.repository.ProductRepository
import id.my.hizari.dummyjsonpreview.domain.stubProduct
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.product.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductWriteUseCaseTest {

    private val repository: ProductRepository = mockk()

    @Test
    fun `getProductDetail returns the requested product`() = runTest(testBody = {
        val expected = stubProduct(id = 7)
        coEvery(stubBlock = { repository.getProduct(id = 7) }) returns expected

        val actual = GetProductDetailUseCase(repository = repository).invoke(id = 7)

        assertEquals(expected, actual)
    })

    @Test
    fun `addProduct forwards the draft and returns the created product`() = runTest(testBody = {
        val draft = ProductDraft(title = "Test Product", price = 99.5)
        val expected = stubProduct(id = 195, title = "Test Product")
        coEvery(stubBlock = { repository.addProduct(draft = draft) }) returns expected

        val actual = AddProductUseCase(repository = repository).invoke(draft = draft)

        assertEquals(expected, actual)
        coVerify(exactly = 1, verifyBlock = { repository.addProduct(draft = draft) })
    })

    @Test
    fun `updateProduct forwards both the id and the draft`() = runTest(testBody = {
        val draft = ProductDraft(title = "Edited Title")
        val expected = stubProduct(id = 1, title = "Edited Title")
        coEvery(stubBlock = { repository.updateProduct(id = 1, draft = draft) }) returns expected

        val actual = UpdateProductUseCase(repository = repository).invoke(id = 1, draft = draft)

        assertEquals(expected, actual)
        coVerify(exactly = 1, verifyBlock = { repository.updateProduct(id = 1, draft = draft) })
    })

    @Test
    fun `deleteProduct returns the deletion receipt`() = runTest(testBody = {
        val expected = DeletedProduct(
            id = 1,
            title = "Essence Mascara Lash Princess",
            isDeleted = true,
            deletedOn = "2026-08-11T08:45:28.040Z"
        )
        coEvery(stubBlock = { repository.deleteProduct(id = 1) }) returns expected

        val actual = DeleteProductUseCase(repository = repository).invoke(id = 1)

        assertEquals(expected, actual)
        assertTrue(actual.isDeleted)
    })
}

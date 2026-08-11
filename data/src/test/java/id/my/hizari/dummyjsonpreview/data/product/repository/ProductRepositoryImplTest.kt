package id.my.hizari.dummyjsonpreview.data.product.repository

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.data.product.api.ProductApi
import id.my.hizari.dummyjsonpreview.data.product.model.DeleteProductResponse
import id.my.hizari.dummyjsonpreview.data.product.model.ProductDto
import id.my.hizari.dummyjsonpreview.data.product.model.ProductListResponse
import id.my.hizari.dummyjsonpreview.data.product.model.ProductRequest
import id.my.hizari.dummyjsonpreview.data.network.ApiErrorMapper
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * id.my.hizari.dummyjsonpreview.data.product.repository
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductRepositoryImplTest {

    private val api: ProductApi = mockk()
    private val repository = ProductRepositoryImpl(
        api = api,
        errorMapper = ApiErrorMapper(gson = Gson())
    )

    @Test
    fun `it maps a product page and forwards the paging window`() = runTest(testBody = {
        coEvery(stubBlock = { api.getProducts(limit = any(), skip = any()) }) returns listResponse()

        val page = repository.getProducts(limit = 20, skip = 40)

        coVerify(exactly = 1, verifyBlock = { api.getProducts(limit = 20, skip = 40) })
        assertEquals(1, page.products.size)
        assertEquals("Essence Mascara", page.products.first().title)
        assertEquals(194, page.total)
    })

    @Test
    fun `it forwards a search query with its paging window`() = runTest(testBody = {
        coEvery(stubBlock = { api.searchProducts(query = any(), limit = any(), skip = any()) }) returns listResponse()

        repository.searchProducts(query = "phone", limit = 20, skip = 20)

        coVerify(exactly = 1, verifyBlock = { api.searchProducts(query = "phone", limit = 20, skip = 20) })
    })

    @Test
    fun `it maps a single product`() = runTest(testBody = {
        coEvery(stubBlock = { api.getProduct(id = 1) }) returns productDto()

        val product = repository.getProduct(id = 1)

        assertEquals(1, product.id)
        assertEquals("Essence Mascara", product.title)
    })

    @Test
    fun `adding a product sends the draft as a request body`() = runTest(testBody = {
        val request = slot<ProductRequest>()
        coEvery(stubBlock = { api.addProduct(capture(lst = request)) }) returns productDto(id = 195, title = "Test Product")

        val product = repository.addProduct(
            draft = ProductDraft(title = "Test Product", price = 99.5, category = "beauty")
        )

        assertEquals("Test Product", request.captured.title)
        assertEquals(99.5, request.captured.price!!, 0.001)
        assertEquals(195, product.id)
    })

    @Test
    fun `updating a product sends both the id and the draft`() = runTest(testBody = {
        val request = slot<ProductRequest>()
        coEvery(stubBlock = { api.updateProduct(id = 1, request = capture(lst = request)) }) returns
            productDto(title = "Edited Title")

        val product = repository.updateProduct(id = 1, draft = ProductDraft(title = "Edited Title"))

        assertEquals("Edited Title", request.captured.title)
        assertEquals("Edited Title", product.title)
    })

    @Test
    fun `deleting a product returns the receipt`() = runTest(testBody = {
        coEvery(stubBlock = { api.deleteProduct(id = 1) }) returns DeleteProductResponse(
            id = 1,
            title = "Essence Mascara",
            isDeleted = true,
            deletedOn = "2026-08-11T08:45:28.040Z"
        )

        val deleted = repository.deleteProduct(id = 1)

        assertTrue(deleted.isDeleted)
        assertEquals(1, deleted.id)
    })

    @Test
    fun `being offline surfaces as a network failure`() = runTest(testBody = {
        coEvery(stubBlock = { api.getProducts(limit = any(), skip = any()) }) throws UnknownHostException()

        assertThrows(AppException.Network::class.java) {
            kotlinx.coroutines.runBlocking(block = { repository.getProducts(limit = 20, skip = 0) })
        }
    })

    @Test
    fun `a slow server surfaces as a timeout`() = runTest(testBody = {
        coEvery(stubBlock = { api.getProducts(limit = any(), skip = any()) }) throws SocketTimeoutException()

        assertThrows(AppException.Timeout::class.java) {
            kotlinx.coroutines.runBlocking(block = { repository.getProducts(limit = 20, skip = 0) })
        }
    })

    @Test
    fun `a missing product surfaces as a http failure carrying the server message`() = runTest(testBody = {
        coEvery(stubBlock = { api.getProduct(id = any()) }) throws HttpException(
            Response.error<Any>(
                404,
                """{"message":"Product with id '99999' not found"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        )

        val thrown = assertThrows(AppException.Http::class.java) {
            kotlinx.coroutines.runBlocking(block = { repository.getProduct(id = 99999) })
        }

        assertEquals(404, thrown.code)
        assertEquals("Product with id '99999' not found", thrown.serverMessage)
    })

    private fun productDto(id: Int = 1, title: String = "Essence Mascara") = ProductDto(
        id = id,
        title = title,
        description = null,
        category = "beauty",
        price = 9.99,
        discountPercentage = null,
        rating = null,
        stock = null,
        tags = null,
        brand = null,
        sku = null,
        weight = null,
        dimensions = null,
        warrantyInformation = null,
        shippingInformation = null,
        availabilityStatus = null,
        reviews = null,
        returnPolicy = null,
        minimumOrderQuantity = null,
        meta = null,
        thumbnail = null,
        images = null
    )

    private fun listResponse() = ProductListResponse(
        products = listOf(productDto()),
        total = 194,
        skip = 0,
        limit = 20
    )
}

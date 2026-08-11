package id.my.hizari.dummyjsonpreview.feature.product.detail

import androidx.lifecycle.SavedStateHandle
import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.product.model.DeletedProduct
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.usecase.DeleteProductUseCase
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductDetailUseCase
import id.my.hizari.dummyjsonpreview.navigation.NavigationArgs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.feature.product.detail
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getProductDetail: GetProductDetailUseCase = mockk()
    private val deleteProduct: DeleteProductUseCase = mockk()

    @Test
    fun `it loads the product it was given as soon as it is created`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.product?.title)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
        coVerify(exactly = 1, verifyBlock = { getProductDetail(id = PRODUCT_ID) })
    })

    /** Without this the loading flag could be set only after the call resolved and never seen. */
    @Test
    fun `it reports loading while the product is being fetched`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) coAnswers {
            delay(timeMillis = 100)
            product()
        }

        val viewModel = viewModel()
        advanceTimeBy(delayTimeMillis = 50)

        assertTrue(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.product)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(PRODUCT_ID, viewModel.state.value.product?.id)
    })

    @Test
    fun `a failed load takes over the screen`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) throws AppException.Network()

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.product)
        assertTrue(viewModel.state.value.showFullScreenError)
    })

    @Test
    fun `retry fetches the product again and clears the error`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) throws AppException.Network()
        val viewModel = viewModel()
        advanceUntilIdle()

        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        viewModel.onRetry()
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
        assertEquals(PRODUCT_ID, viewModel.state.value.product?.id)
        coVerify(exactly = 2, verifyBlock = { getProductDetail(id = PRODUCT_ID) })
    })

    /** Deleting is destructive, so nothing may reach the API before the user confirms. */
    @Test
    fun `tapping delete only asks for confirmation`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isDeleteDialogVisible)
        assertNull(viewModel.state.value.deletedTitle)
        coVerify(exactly = 0, verifyBlock = { deleteProduct(id = any()) })
    })

    @Test
    fun `dismissing the confirmation deletes nothing`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        assertTrue(viewModel.state.value.isDeleteDialogVisible)

        viewModel.onDeleteDismiss()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isDeleteDialogVisible)
        coVerify(exactly = 0, verifyBlock = { deleteProduct(id = any()) })
    })

    @Test
    fun `confirming reports the deleted product and closes the confirmation`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { deleteProduct(id = any()) }) returns deleted(title = "Essence Mascara Lash Princess")
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()
        advanceUntilIdle()

        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.deletedTitle)
        assertNotNull(viewModel.state.value.deletedTitle)
        assertFalse(viewModel.state.value.isDeleteDialogVisible)
        assertFalse(viewModel.state.value.isDeleting)
        assertNull(viewModel.state.value.deleteErrorMessage)
        coVerify(exactly = 1, verifyBlock = { deleteProduct(id = PRODUCT_ID) })
    })

    /** Guards against the delete flag being set and cleared without ever being observable. */
    @Test
    fun `it reports deleting while the call is in flight`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { deleteProduct(id = any()) }) coAnswers {
            delay(timeMillis = 100)
            deleted(title = "Essence Mascara Lash Princess")
        }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()
        advanceTimeBy(delayTimeMillis = 50)

        assertTrue(viewModel.state.value.isDeleting)
        assertNull(viewModel.state.value.deletedTitle)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isDeleting)
        assertNotNull(viewModel.state.value.deletedTitle)
    })

    /** A second tap while the request is running would delete twice. */
    @Test
    fun `confirming twice only deletes once`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { deleteProduct(id = any()) }) coAnswers {
            delay(timeMillis = 100)
            deleted(title = "Essence Mascara Lash Princess")
        }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()
        viewModel.onDeleteConfirm()
        advanceUntilIdle()

        coVerify(exactly = 1, verifyBlock = { deleteProduct(id = PRODUCT_ID) })
    })

    /** The product is still there, so the failure belongs in the dialog rather than on the screen. */
    @Test
    fun `a failed delete keeps the product and reports the error in the dialog`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { deleteProduct(id = any()) }) throws AppException.Network()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.deleteErrorMessage)
        assertTrue(viewModel.state.value.isDeleteDialogVisible)
        assertFalse(viewModel.state.value.isDeleting)
        assertNull(viewModel.state.value.deletedTitle)
        assertEquals(PRODUCT_ID, viewModel.state.value.product?.id)
        assertNull(viewModel.state.value.errorMessage)
    })

    @Test
    fun `dismissing after a failed delete clears the error`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { deleteProduct(id = any()) }) throws AppException.Network()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()
        advanceUntilIdle()
        assertEquals("Network unavailable", viewModel.state.value.deleteErrorMessage)

        viewModel.onDeleteDismiss()

        assertNull(viewModel.state.value.deleteErrorMessage)
        assertFalse(viewModel.state.value.isDeleteDialogVisible)
    })

    /** DummyJSON echoes the title back, but the response type allows it to be missing. */
    @Test
    fun `the deleted title falls back to the loaded product`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { deleteProduct(id = any()) }) returns deleted(title = null)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()
        advanceUntilIdle()

        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.deletedTitle)
    })

    private fun viewModel() = ProductDetailViewModel(
        savedStateHandle = SavedStateHandle(
            initialState = mapOf(NavigationArgs.PRODUCT_ID to PRODUCT_ID)
        ),
        getProductDetailUseCase = getProductDetail,
        deleteProductUseCase = deleteProduct
    )

    private fun product() = Product(
        id = PRODUCT_ID,
        title = "Essence Mascara Lash Princess",
        description = "The Essence Mascara Lash Princess is a popular mascara.",
        category = "beauty",
        price = 9.99,
        discountPercentage = 7.17,
        rating = 4.94,
        stock = 5,
        tags = listOf("beauty", "mascara"),
        brand = "Essence",
        sku = "RCH45Q1A",
        weight = 2.0,
        dimensions = null,
        warrantyInformation = "1 month warranty",
        shippingInformation = "Ships in 1 month",
        availabilityStatus = "Low Stock",
        returnPolicy = "30 days return policy",
        minimumOrderQuantity = 24,
        meta = null,
        thumbnail = null
    )

    private fun deleted(title: String?) = DeletedProduct(
        id = PRODUCT_ID,
        title = title,
        isDeleted = true,
        deletedOn = "2026-08-11T00:00:00.000Z"
    )

    private companion object {
        const val PRODUCT_ID = 1
    }
}

package id.my.hizari.dummyjsonpreview.feature.product.edit

import androidx.lifecycle.SavedStateHandle
import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductDetailUseCase
import id.my.hizari.dummyjsonpreview.domain.product.usecase.UpdateProductUseCase
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductFormError
import id.my.hizari.dummyjsonpreview.navigation.NavigationArgs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
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
 * id.my.hizari.dummyjsonpreview.feature.product.edit
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@OptIn(ExperimentalCoroutinesApi::class)
class ProductEditViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getProductDetail: GetProductDetailUseCase = mockk()
    private val updateProduct: UpdateProductUseCase = mockk()

    @Test
    fun `it fills the form from the product it was given`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.title)
        assertEquals("beauty", viewModel.state.value.category)
        assertEquals("9.99", viewModel.state.value.price)
        assertEquals("7.17", viewModel.state.value.discountPercentage)
        assertEquals("5", viewModel.state.value.stock)
        assertEquals("Essence", viewModel.state.value.brand)
        assertFalse(viewModel.state.value.isLoading)
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
        assertEquals("", viewModel.state.value.title)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.title)
    })

    @Test
    fun `a failed load takes over the screen`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) throws AppException.Network()

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.loadErrorMessage)
        assertFalse(viewModel.state.value.isLoading)
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

        assertNull(viewModel.state.value.loadErrorMessage)
        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.title)
        coVerify(exactly = 2, verifyBlock = { getProductDetail(id = PRODUCT_ID) })
    })

    /** Editing shares the form rules, so a cleared title must block the update too. */
    @Test
    fun `an invalid form reports the errors and updates nothing`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onTitleChange(title = "")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals(ProductFormError.REQUIRED, viewModel.state.value.titleError)
        assertNull(viewModel.state.value.savedTitle)
        coVerify(exactly = 0, verifyBlock = { updateProduct(id = any(), draft = any()) })
    })

    @Test
    fun `submitting sends the id and the edited draft`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        val draft = slot<ProductDraft>()
        coEvery(
            stubBlock = { updateProduct(id = PRODUCT_ID, draft = capture(draft)) }
        ) returns product().copy(title = "Mascara Deluxe")
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onTitleChange(title = "Mascara Deluxe")
        viewModel.onPriceChange(price = "12.50")
        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Mascara Deluxe", draft.captured.title)
        assertEquals(12.50, draft.captured.price!!, 0.001)
        assertEquals("Mascara Deluxe", viewModel.state.value.savedTitle)
        assertFalse(viewModel.state.value.isSubmitting)
        coVerify(exactly = 1, verifyBlock = { updateProduct(id = PRODUCT_ID, draft = any()) })
    })

    @Test
    fun `it reports submitting while the update is in flight`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { updateProduct(id = any(), draft = any()) }) coAnswers {
            delay(timeMillis = 100)
            product()
        }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onSubmit()
        advanceTimeBy(delayTimeMillis = 50)

        assertTrue(viewModel.state.value.isSubmitting)
        assertNull(viewModel.state.value.savedTitle)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSubmitting)
        assertNotNull(viewModel.state.value.savedTitle)
    })

    @Test
    fun `submitting twice only updates once`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { updateProduct(id = any(), draft = any()) }) coAnswers {
            delay(timeMillis = 100)
            product()
        }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onSubmit()
        viewModel.onSubmit()
        advanceUntilIdle()

        coVerify(exactly = 1, verifyBlock = { updateProduct(id = any(), draft = any()) })
    })

    /** A failed update is not a failed load, so the form stays on screen with its values. */
    @Test
    fun `a failed update reports the error and keeps the form`() = runTest(testBody = {
        coEvery(stubBlock = { getProductDetail(id = any()) }) returns product()
        coEvery(stubBlock = { updateProduct(id = any(), draft = any()) }) throws AppException.Network()
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.submitErrorMessage)
        assertNull(viewModel.state.value.loadErrorMessage)
        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.title)
        assertNull(viewModel.state.value.savedTitle)
        assertFalse(viewModel.state.value.showFullScreenError)
    })

    private fun viewModel() = ProductEditViewModel(
        savedStateHandle = SavedStateHandle(
            initialState = mapOf(NavigationArgs.PRODUCT_ID to PRODUCT_ID)
        ),
        getProductDetailUseCase = getProductDetail,
        updateProductUseCase = updateProduct
    )

    private fun product() = Product(
        id = PRODUCT_ID,
        title = "Essence Mascara Lash Princess",
        description = "A popular mascara.",
        category = "beauty",
        price = 9.99,
        discountPercentage = 7.17,
        rating = 4.94,
        stock = 5,
        brand = "Essence",
        sku = "BEA-ESS-ESS-001",
        weight = 4.0,
        dimensions = null,
        warrantyInformation = null,
        shippingInformation = null,
        availabilityStatus = null,
        returnPolicy = null,
        minimumOrderQuantity = null,
        meta = null,
        thumbnail = null
    )

    private companion object {
        const val PRODUCT_ID = 1
    }
}

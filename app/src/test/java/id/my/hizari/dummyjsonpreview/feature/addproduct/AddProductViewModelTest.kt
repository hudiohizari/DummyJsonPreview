package id.my.hizari.dummyjsonpreview.feature.addproduct

import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import id.my.hizari.dummyjsonpreview.domain.product.usecase.AddProductUseCase
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductFormError
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.feature.addproduct
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val addProduct: AddProductUseCase = mockk()

    /** Nothing may reach the API until the form is valid. */
    @Test
    fun `an invalid form reports the errors and saves nothing`() = runTest(testBody = {
        val viewModel = AddProductViewModel(addProductUseCase = addProduct)

        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals(ProductFormError.REQUIRED, viewModel.state.value.titleError)
        assertEquals(ProductFormError.REQUIRED, viewModel.state.value.priceError)
        assertFalse(viewModel.state.value.isSaved)
        coVerify(exactly = 0, verifyBlock = { addProduct(draft = any()) })
    })

    @Test
    fun `typing into a field clears its error`() = runTest(testBody = {
        val viewModel = AddProductViewModel(addProductUseCase = addProduct)
        viewModel.onSubmit()
        advanceUntilIdle()
        assertEquals(ProductFormError.REQUIRED, viewModel.state.value.titleError)

        viewModel.onTitleChange(title = "Mascara")

        assertNull(viewModel.state.value.titleError)
        assertEquals(ProductFormError.REQUIRED, viewModel.state.value.priceError)
    })

    @Test
    fun `a valid form sends the typed draft and reports the saved product`() = runTest(testBody = {
        val draft = slot<ProductDraft>()
        coEvery(stubBlock = { addProduct(draft = capture(draft)) }) returns product()
        val viewModel = fillValidForm()

        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Mascara", draft.captured.title)
        assertEquals(9.99, draft.captured.price!!, 0.001)
        assertEquals("beauty", draft.captured.category)
        assertEquals("Essence Mascara Lash Princess", viewModel.state.value.savedTitle)
        assertTrue(viewModel.state.value.isSaved)
        assertFalse(viewModel.state.value.isSubmitting)
        coVerify(exactly = 1, verifyBlock = { addProduct(draft = any()) })
    })

    /** Guards against the flag being set and cleared without ever being observable. */
    @Test
    fun `it reports submitting while the call is in flight`() = runTest(testBody = {
        coEvery(stubBlock = { addProduct(draft = any()) }) coAnswers {
            delay(timeMillis = 100)
            product()
        }
        val viewModel = fillValidForm()

        viewModel.onSubmit()
        advanceTimeBy(delayTimeMillis = 50)

        assertTrue(viewModel.state.value.isSubmitting)
        assertFalse(viewModel.state.value.isSaved)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSubmitting)
        assertTrue(viewModel.state.value.isSaved)
    })

    @Test
    fun `submitting twice only saves once`() = runTest(testBody = {
        coEvery(stubBlock = { addProduct(draft = any()) }) coAnswers {
            delay(timeMillis = 100)
            product()
        }
        val viewModel = fillValidForm()

        viewModel.onSubmit()
        viewModel.onSubmit()
        advanceUntilIdle()

        coVerify(exactly = 1, verifyBlock = { addProduct(draft = any()) })
    })

    /** The typed values stay put so the user can retry without filling the form again. */
    @Test
    fun `a failed save reports the error and keeps the form`() = runTest(testBody = {
        coEvery(stubBlock = { addProduct(draft = any()) }) throws AppException.Network()
        val viewModel = fillValidForm()

        viewModel.onSubmit()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.submitErrorMessage)
        assertEquals("Mascara", viewModel.state.value.title)
        assertEquals("9.99", viewModel.state.value.price)
        assertFalse(viewModel.state.value.isSubmitting)
        assertFalse(viewModel.state.value.isSaved)
    })

    @Test
    fun `resubmitting clears the previous failure`() = runTest(testBody = {
        coEvery(stubBlock = { addProduct(draft = any()) }) throws AppException.Network()
        val viewModel = fillValidForm()
        viewModel.onSubmit()
        advanceUntilIdle()
        assertEquals("Network unavailable", viewModel.state.value.submitErrorMessage)

        coEvery(stubBlock = { addProduct(draft = any()) }) returns product()
        viewModel.onSubmit()
        advanceUntilIdle()

        assertNull(viewModel.state.value.submitErrorMessage)
        assertTrue(viewModel.state.value.isSaved)
    })

    /** Keyboard type only hints the soft keyboard, so a paste can still put anything in a field. */
    @Test
    fun `the number fields refuse anything that is not a number`() = runTest(testBody = {
        val viewModel = AddProductViewModel(addProductUseCase = addProduct)

        viewModel.onPriceChange(price = "9.9.9abc-5")
        viewModel.onDiscountChange(discountPercentage = "7,5%")
        viewModel.onStockChange(stock = "1.5kg")

        assertEquals("9.995", viewModel.state.value.price)
        assertEquals("7.5", viewModel.state.value.discountPercentage)
        assertEquals("15", viewModel.state.value.stock)
    })

    /** Adding is a tab with nothing to go back to, so the form clears for the next product. */
    @Test
    fun `acknowledging the save empties the form`() = runTest(testBody = {
        coEvery(stubBlock = { addProduct(draft = any()) }) returns product()
        val viewModel = fillValidForm()
        viewModel.onSubmit()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isSaved)

        viewModel.onSavedAcknowledged()

        assertFalse(viewModel.state.value.isSaved)
        assertNull(viewModel.state.value.savedTitle)
        assertEquals("", viewModel.state.value.title)
        assertEquals("", viewModel.state.value.price)
        assertEquals("", viewModel.state.value.category)
    })

    private fun fillValidForm() = AddProductViewModel(addProductUseCase = addProduct).apply {
        onTitleChange(title = "Mascara")
        onPriceChange(price = "9.99")
        onCategoryChange(category = "beauty")
    }

    private fun product() = Product(
        id = 195,
        title = "Essence Mascara Lash Princess",
        description = null,
        category = "beauty",
        price = 9.99,
        discountPercentage = null,
        rating = null,
        stock = null,
        brand = null,
        sku = null,
        weight = null,
        dimensions = null,
        warrantyInformation = null,
        shippingInformation = null,
        availabilityStatus = null,
        returnPolicy = null,
        minimumOrderQuantity = null,
        meta = null,
        thumbnail = null
    )
}

package id.my.hizari.dummyjsonpreview.feature.product.list

import id.my.hizari.dummyjsonpreview.MainDispatcherRule
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductPage
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.net.UnknownHostException

/**
 * id.my.hizari.dummyjsonpreview.feature.product.list
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getProducts: GetProductsUseCase = mockk()
    private val observeCurrentUser: ObserveCurrentUserUseCase = mockk()

    @Test
    fun `it loads the first page as soon as it is created`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals(20, viewModel.state.value.products.size)
        assertEquals(194, viewModel.state.value.total)
        assertFalse(viewModel.state.value.isLoading)
        coVerify(exactly = 1, verifyBlock = { getProducts(query = "", limit = PAGE_SIZE, skip = 0) })
    })

    @Test
    fun `the greeting comes from the signed in user`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Emily Johnson", viewModel.state.value.greetingName)
    })

    /** Without the debounce every keystroke would fire a request. */
    @Test
    fun `typing is debounced into a single search`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onQueryChange(query = "p")
        viewModel.onQueryChange(query = "ph")
        viewModel.onQueryChange(query = "pho")
        viewModel.onQueryChange(query = "phone")
        advanceTimeBy(delayTimeMillis = DEBOUNCE_MILLIS - 100)
        coVerify(exactly = 0, verifyBlock = { getProducts(query = "phone", any(), any()) })

        advanceUntilIdle()

        coVerify(
            exactly = 1,
            verifyBlock = { getProducts(query = "phone", limit = PAGE_SIZE, skip = 0) }
        )
    })

    /** Clearing the field should restore the catalogue at once, not after a pause. */
    @Test
    fun `clearing the query reloads the catalogue without waiting`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.onQueryChange(query = "phone")
        advanceUntilIdle()

        viewModel.onClearQuery()
        advanceTimeBy(delayTimeMillis = 1)
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.query)
        coVerify(exactly = 2, verifyBlock = { getProducts(query = "", limit = PAGE_SIZE, skip = 0) })
    })

    @Test
    fun `the next page is appended and skip follows the loaded count`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), skip = 0) }) returns page(count = 20, skip = 0)
        coEvery(stubBlock = { getProducts(any(), any(), skip = 20) }) returns page(count = 20, skip = 20)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLoadNextPage()
        advanceUntilIdle()

        assertEquals(40, viewModel.state.value.products.size)
        coVerify(exactly = 1, verifyBlock = { getProducts(query = "", limit = PAGE_SIZE, skip = 20) })
    })

    @Test
    fun `it stops paging once the last page has arrived`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 5, total = 5)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLoadNextPage()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.hasMore)
        coVerify(exactly = 1, verifyBlock = { getProducts(any(), any(), any()) })
    })

    /** A failed page must not wipe what the user is already looking at. */
    @Test
    fun `a pagination failure keeps the products already loaded`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), skip = 0) }) returns page(count = 20, skip = 0)
        coEvery(stubBlock = { getProducts(any(), any(), skip = 20) }) throws AppException.Network(
            cause = UnknownHostException()
        )
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLoadNextPage()
        advanceUntilIdle()

        assertEquals(20, viewModel.state.value.products.size)
        assertEquals("Network unavailable", viewModel.state.value.paginationErrorMessage)
        assertNull(viewModel.state.value.errorMessage)
    })

    /** The row shows a retry, so a failed page has to be loadable again without a full reload. */
    @Test
    fun `a failed page can be retried and appends on success`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), skip = 0) }) returns page(count = 20, skip = 0)
        coEvery(stubBlock = { getProducts(any(), any(), skip = 20) }) throws AppException.Network(
            cause = UnknownHostException()
        )
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.onLoadNextPage()
        advanceUntilIdle()
        assertEquals("Network unavailable", viewModel.state.value.paginationErrorMessage)

        coEvery(stubBlock = { getProducts(any(), any(), skip = 20) }) returns page(count = 20, skip = 20)
        viewModel.onLoadNextPage()
        advanceUntilIdle()

        assertEquals(40, viewModel.state.value.products.size)
        assertNull(viewModel.state.value.paginationErrorMessage)
    })

    /** A failed page must never replace the list with a full screen error. */
    @Test
    fun `a pagination failure does not raise a screen level error`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), skip = 0) }) returns page(count = 20, skip = 0)
        coEvery(stubBlock = { getProducts(any(), any(), skip = 20) }) throws AppException.Network(
            cause = UnknownHostException()
        )
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onLoadNextPage()
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isEmpty)
        assertTrue(viewModel.state.value.hasMore)
    })

    /** Wiping the list mid-refresh would flash an empty screen under the spinner. */
    @Test
    fun `refresh keeps the visible products while it reloads`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onRefresh()

        assertTrue(viewModel.state.value.isRefreshing)
        assertEquals(20, viewModel.state.value.products.size)
        assertFalse(viewModel.state.value.isLoading)
    })

    @Test
    fun `refresh replaces the list and clears the refreshing flag`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()

        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 5, total = 5)
        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(5, viewModel.state.value.products.size)
        assertFalse(viewModel.state.value.isRefreshing)
    })

    /** This is the case where an error and real data are on screen together. */
    @Test
    fun `a failed refresh keeps the products and reports the error`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()

        coEvery(stubBlock = { getProducts(any(), any(), any()) }) throws AppException.Network(
            cause = UnknownHostException()
        )
        viewModel.onRefresh()
        advanceUntilIdle()

        assertEquals(20, viewModel.state.value.products.size)
        assertEquals("Network unavailable", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isRefreshing)
        // Data is present, so the error must not take over the screen.
        assertFalse(viewModel.state.value.showFullScreenError)
    })

    @Test
    fun `a first page failure with no data does take over the screen`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) throws AppException.Network(
            cause = UnknownHostException()
        )

        val viewModel = viewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.showFullScreenError)
    })

    @Test
    fun `refresh is ignored while one is already running`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        viewModel.onRefresh()
        advanceUntilIdle()

        // one initial load plus a single refresh
        coVerify(exactly = 2, verifyBlock = { getProducts(any(), any(), skip = 0) })
    })

    @Test
    fun `a first page failure surfaces as a screen level error`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) throws AppException.Network(
            cause = UnknownHostException()
        )

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.state.value.errorMessage)
        assertTrue(viewModel.state.value.products.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
    })

    @Test
    fun `retry reloads the first page`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) throws AppException.Network(
            cause = UnknownHostException()
        )
        val viewModel = viewModel()
        advanceUntilIdle()

        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        viewModel.onRetry()
        advanceUntilIdle()

        assertEquals(20, viewModel.state.value.products.size)
        assertNull(viewModel.state.value.errorMessage)
    })

    @Test
    fun `an empty search reports empty rather than an error`() = runTest(testBody = {
        stubUser()
        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 20)
        val viewModel = viewModel()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isEmpty)

        coEvery(stubBlock = { getProducts(any(), any(), any()) }) returns page(count = 0, total = 0)
        viewModel.onQueryChange(query = "zzzzqqq")
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isEmpty)
        assertTrue(viewModel.state.value.products.isEmpty())
        assertFalse(viewModel.state.value.hasMore)
        assertNull(viewModel.state.value.errorMessage)
        coVerify(exactly = 1, verifyBlock = { getProducts(query = "zzzzqqq", any(), any()) })
    })

    private fun viewModel() = ProductListViewModel(
        getProductsUseCase = getProducts,
        observeCurrentUserUseCase = observeCurrentUser
    )

    private fun stubUser() {
        every(stubBlock = { observeCurrentUser() }) returns flowOf(
            User(
                id = 1,
                username = "emilys",
                email = "emily.johnson@x.dummyjson.com",
                firstName = "Emily",
                lastName = "Johnson",
                gender = "female",
                image = null
            )
        )
    }

    private fun page(count: Int, total: Int = 194, skip: Int = 0) = ProductPage(
        products = List(size = count, init = { index -> product(id = skip + index) }),
        total = total,
        skip = skip,
        limit = PAGE_SIZE
    )

    private fun product(id: Int) = Product(
        id = id,
        title = "Product $id",
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

    private companion object {
        const val PAGE_SIZE = GetProductsUseCase.PAGE_SIZE
        const val DEBOUNCE_MILLIS = ProductListViewModel.DEBOUNCE_MILLIS
    }
}

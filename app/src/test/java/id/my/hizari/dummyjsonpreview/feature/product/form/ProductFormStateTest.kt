package id.my.hizari.dummyjsonpreview.feature.product.form

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.feature.product.form
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductFormStateTest {

    @Test
    fun `a blank title is required`() {
        val validated = ProductFormState(title = "   ", price = "9.99").validated()

        assertEquals(ProductFormError.REQUIRED, validated.titleError)
        assertTrue(validated.hasErrors)
    }

    @Test
    fun `a blank price is required`() {
        val validated = ProductFormState(title = "Mascara", price = "").validated()

        assertEquals(ProductFormError.REQUIRED, validated.priceError)
    }

    @Test
    fun `a price that is not a number is rejected`() {
        val validated = ProductFormState(title = "Mascara", price = "nine").validated()

        assertEquals(ProductFormError.INVALID_NUMBER, validated.priceError)
    }

    /** A free product is not something this form should be able to submit. */
    @Test
    fun `a price of zero or less is rejected`() {
        assertEquals(
            ProductFormError.INVALID_NUMBER,
            ProductFormState(title = "Mascara", price = "0").validated().priceError
        )
        assertEquals(
            ProductFormError.INVALID_NUMBER,
            ProductFormState(title = "Mascara", price = "-1").validated().priceError
        )
    }

    @Test
    fun `every broken field is reported in one pass`() {
        val validated = ProductFormState(
            title = "",
            price = "abc",
            discountPercentage = "150",
            stock = "-3"
        ).validated()

        assertEquals(ProductFormError.REQUIRED, validated.titleError)
        assertEquals(ProductFormError.INVALID_NUMBER, validated.priceError)
        assertEquals(ProductFormError.PERCENT_RANGE, validated.discountError)
        assertEquals(ProductFormError.INVALID_NUMBER, validated.stockError)
    }

    /** Infinity would pass a greater than zero check and then break JSON serialisation. */
    @Test
    fun `a price too large to hold is rejected`() {
        val validated = ProductFormState(
            title = "Mascara",
            price = "9".repeat(n = 400)
        ).validated()

        assertEquals(ProductFormError.INVALID_NUMBER, validated.priceError)
    }

    /** A comma is what the keyboard offers in this locale, so it has to mean a decimal point. */
    @Test
    fun `a price typed with a comma is accepted`() {
        val validated = ProductFormState(title = "Mascara", price = "9,99").validated()

        assertNull(validated.priceError)
        assertEquals(9.99, validated.toDraft().price!!, 0.001)
    }

    @Test
    fun `a discount typed with a comma is accepted`() {
        val validated = valid(discountPercentage = "7,5").validated()

        assertNull(validated.discountError)
        assertEquals(7.5, validated.toDraft().discountPercentage!!, 0.001)
    }

    @Test
    fun `a discount outside nought to one hundred is rejected`() {
        assertEquals(
            ProductFormError.PERCENT_RANGE,
            valid(discountPercentage = "101").validated().discountError
        )
        assertEquals(
            ProductFormError.PERCENT_RANGE,
            valid(discountPercentage = "-1").validated().discountError
        )
        assertNull(valid(discountPercentage = "100").validated().discountError)
        assertNull(valid(discountPercentage = "0").validated().discountError)
    }

    @Test
    fun `a stock that is negative or fractional is rejected`() {
        assertEquals(ProductFormError.INVALID_NUMBER, valid(stock = "-1").validated().stockError)
        assertEquals(ProductFormError.INVALID_NUMBER, valid(stock = "1.5").validated().stockError)
        assertNull(valid(stock = "0").validated().stockError)
    }

    /** Only title and price are mandatory, so the rest may be left empty. */
    @Test
    fun `blank optional fields are accepted`() {
        val validated = ProductFormState(
            title = "Mascara",
            price = "9.99",
            discountError = ProductFormError.PERCENT_RANGE,
            stockError = ProductFormError.INVALID_NUMBER
        ).validated()

        assertNull(validated.discountError)
        assertNull(validated.stockError)
        assertFalse(validated.hasErrors)
    }

    @Test
    fun `validating clears errors that no longer apply`() {
        val withError = ProductFormState(
            title = "Mascara",
            price = "9.99",
            titleError = ProductFormError.REQUIRED,
            priceError = ProductFormError.INVALID_NUMBER
        )

        val validated = withError.validated()

        assertNull(validated.titleError)
        assertNull(validated.priceError)
        assertFalse(validated.hasErrors)
    }

    @Test
    fun `the draft carries the typed values`() {
        val draft = ProductFormState(
            title = "  Mascara  ",
            description = "A description",
            category = "beauty",
            price = "9.99",
            discountPercentage = "7.5",
            stock = "12",
            brand = "Essence"
        ).toDraft()

        assertEquals("Mascara", draft.title)
        assertEquals("A description", draft.description)
        assertEquals("beauty", draft.category)
        assertEquals(9.99, draft.price!!, 0.001)
        assertEquals(7.5, draft.discountPercentage!!, 0.001)
        assertEquals(12, draft.stock)
        assertEquals("Essence", draft.brand)
    }

    /** Sending empty strings would overwrite existing values with blanks on an edit. */
    @Test
    fun `blank optional fields are left out of the draft`() {
        val draft = ProductFormState(title = "Mascara", price = "9.99").toDraft()

        assertEquals("Mascara", draft.title)
        assertEquals(9.99, draft.price!!, 0.001)
        assertNull(draft.description)
        assertNull(draft.category)
        assertNull(draft.discountPercentage)
        assertNull(draft.stock)
        assertNull(draft.brand)
    }

    @Test
    fun `a load failure only takes over the screen when there is nothing to show`() {
        assertTrue(
            ProductFormState(loadErrorMessage = "Network unavailable").showFullScreenError
        )
        assertFalse(
            ProductFormState(
                title = "Mascara",
                loadErrorMessage = "Network unavailable"
            ).showFullScreenError
        )
    }

    private fun valid(
        discountPercentage: String = "",
        stock: String = ""
    ) = ProductFormState(
        title = "Mascara",
        price = "9.99",
        discountPercentage = discountPercentage,
        stock = stock
    )
}

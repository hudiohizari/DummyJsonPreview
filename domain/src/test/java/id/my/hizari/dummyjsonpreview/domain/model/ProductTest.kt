package id.my.hizari.dummyjsonpreview.domain.model

import id.my.hizari.dummyjsonpreview.domain.stubProduct
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductTest {

    @Test
    fun `discountedPrice applies the discount percentage`() {
        val product = stubProduct(price = 100.0, discountPercentage = 10.0)

        assertEquals(90.0, product.discountedPrice, DELTA)
    }

    @Test
    fun `discountedPrice returns the full price when there is no discount`() {
        val product = stubProduct(price = 100.0, discountPercentage = null)

        assertEquals(100.0, product.discountedPrice, DELTA)
    }

    @Test
    fun `discountedPrice returns the full price when the discount is zero`() {
        val product = stubProduct(price = 49.99, discountPercentage = 0.0)

        assertEquals(49.99, product.discountedPrice, DELTA)
    }

    @Test
    fun `discountedPrice handles fractional percentages`() {
        val product = stubProduct(price = 9.99, discountPercentage = 7.17)

        assertEquals(9.273, product.discountedPrice, DELTA)
    }

    private companion object {
        const val DELTA = 0.001
    }
}

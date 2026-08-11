package id.my.hizari.dummyjsonpreview.domain.product.model

import id.my.hizari.dummyjsonpreview.domain.stubProductPage
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.product.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductPageTest {

    @Test
    fun `hasMore is true when the loaded window is short of the total`() {
        val page = stubProductPage(count = 20, total = 194, skip = 0)

        assertTrue(page.hasMore)
    }

    @Test
    fun `hasMore is false on the last page`() {
        val page = stubProductPage(count = 14, total = 194, skip = 180)

        assertFalse(page.hasMore)
    }

    @Test
    fun `hasMore is false when the page came back empty`() {
        val page = stubProductPage(count = 0, total = 194, skip = 194)

        assertFalse(page.hasMore)
    }

    @Test
    fun `hasMore is false when a search returned nothing`() {
        val page = stubProductPage(count = 0, total = 0, skip = 0)

        assertFalse(page.hasMore)
    }
}

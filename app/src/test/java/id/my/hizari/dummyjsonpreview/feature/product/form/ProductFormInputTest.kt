package id.my.hizari.dummyjsonpreview.feature.product.form

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.feature.product.form
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductFormInputTest {

    @Test
    fun `a decimal field keeps digits and drops everything else`() {
        assertEquals("99", "abc9d9".filteredAsDecimal())
        assertEquals("5", "-5".filteredAsDecimal())
        assertEquals("", "abc".filteredAsDecimal())
        assertEquals("19.99", "19.99".filteredAsDecimal())
    }

    /** Typing a second separator would leave a value nothing can parse. */
    @Test
    fun `a decimal field keeps only the first separator`() {
        assertEquals("9.99", "9.9.9".filteredAsDecimal())
        assertEquals("1.23", "1.2.3.".filteredAsDecimal())
    }

    /** Locales such as this one offer a comma, so it has to mean the same thing. */
    @Test
    fun `a decimal field turns a comma into a dot`() {
        assertEquals("9.99", "9,99".filteredAsDecimal())
        assertEquals("9.99", "9,9.9".filteredAsDecimal())
    }

    @Test
    fun `an integer field keeps only digits`() {
        assertEquals("15", "1.5".filteredAsInteger())
        assertEquals("3", "-3".filteredAsInteger())
        assertEquals("12", "12abc".filteredAsInteger())
    }

    @Test
    fun `parsing accepts a comma as the decimal separator`() {
        assertEquals(9.99, "9,99".toFormDouble()!!, 0.001)
        assertEquals(9.99, " 9.99 ".toFormDouble()!!, 0.001)
    }

    /** Infinity passes a greater than zero check and then breaks JSON serialisation. */
    @Test
    fun `parsing rejects a value too large to hold`() {
        assertNull("9".repeat(n = 400).toFormDouble())
    }

    @Test
    fun `parsing rejects text`() {
        assertNull("abc".toFormDouble())
        assertNull("".toFormDouble())
        assertNull("1.5".toFormInt())
        assertNull("abc".toFormInt())
    }

    @Test
    fun `parsing reads a whole number`() {
        assertEquals(12, " 12 ".toFormInt())
        assertEquals(0, "0".toFormInt())
    }
}

package id.my.hizari.dummyjsonpreview.data.product.mapper

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.data.product.model.DeleteProductResponse
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ProductWriteMapperTest {

    private val gson = Gson()

    @Test
    fun `it maps the delete receipt`() {
        val response = gson.fromJson(
            """{"id":1,"title":"Essence Mascara","isDeleted":true,"deletedOn":"2026-08-11T08:45:28.040Z"}""",
            DeleteProductResponse::class.java
        )

        val deleted = response.toDomain()

        assertEquals(1, deleted.id)
        assertEquals("Essence Mascara", deleted.title)
        assertTrue(deleted.isDeleted)
        assertEquals("2026-08-11T08:45:28.040Z", deleted.deletedOn)
    }

    @Test
    fun `a delete receipt without the flag is treated as not deleted`() {
        val response = gson.fromJson("""{"id":1}""", DeleteProductResponse::class.java)

        val deleted = response.toDomain()

        assertFalse(deleted.isDeleted)
    }

    @Test
    fun `a draft becomes a request carrying every populated field`() {
        val draft = ProductDraft(
            title = "Test Product",
            description = "desc",
            category = "beauty",
            price = 99.5,
            discountPercentage = 5.0,
            stock = 7,
            brand = "ACME"
        )

        val request = draft.toRequest()

        assertEquals("Test Product", request.title)
        assertEquals("desc", request.description)
        assertEquals("beauty", request.category)
        assertEquals(99.5, request.price!!, 0.001)
        assertEquals(5.0, request.discountPercentage!!, 0.001)
        assertEquals(7, request.stock)
        assertEquals("ACME", request.brand)
    }

    /**
     * Unset fields must stay null so Gson drops them and the update behaves as a partial edit.
     */
    @Test
    fun `a partial draft leaves untouched fields out of the serialized body`() {
        val draft = ProductDraft(title = "Edited Title")

        val request = draft.toRequest()
        val json = gson.toJson(request)

        assertEquals("Edited Title", request.title)
        assertNull(request.price)
        assertEquals("""{"title":"Edited Title"}""", json)
    }
}

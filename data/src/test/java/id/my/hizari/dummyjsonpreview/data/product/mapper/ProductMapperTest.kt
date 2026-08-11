package id.my.hizari.dummyjsonpreview.data.product.mapper

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.data.product.model.ProductDto
import id.my.hizari.dummyjsonpreview.data.product.model.ProductListResponse
import org.junit.Assert.assertEquals
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

class ProductMapperTest {

    private val gson = Gson()

    @Test
    fun `it maps a complete product including every nested structure`() {
        val dto = gson.fromJson(FULL_PRODUCT_JSON, ProductDto::class.java)

        val product = dto.toDomain()

        assertEquals(1, product.id)
        assertEquals("Essence Mascara Lash Princess", product.title)
        assertEquals("beauty", product.category)
        assertEquals(9.99, product.price, DELTA)
        assertEquals(7.17, product.discountPercentage!!, DELTA)
        assertEquals(4.94, product.rating!!, DELTA)
        assertEquals(5, product.stock)
        assertEquals("Essence", product.brand)
        assertEquals("RCH45Q1A", product.sku)
        assertEquals(listOf("beauty", "mascara"), product.tags)
        assertEquals(listOf("https://cdn.dummyjson.com/p/1/1.png"), product.images)
        assertEquals("https://cdn.dummyjson.com/p/1/thumbnail.png", product.thumbnail)
        assertEquals("1 month warranty", product.warrantyInformation)
        assertEquals("Low Stock", product.availabilityStatus)
        assertEquals(24, product.minimumOrderQuantity)

        assertEquals(23.17, product.dimensions!!.width, DELTA)
        assertEquals(14.43, product.dimensions!!.height, DELTA)
        assertEquals(28.01, product.dimensions!!.depth, DELTA)

        assertEquals(1, product.reviews.size)
        assertEquals(2, product.reviews.first().rating)
        assertEquals("Very unhappy with my purchase!", product.reviews.first().comment)
        assertEquals("John Doe", product.reviews.first().reviewerName)

        assertEquals("9164035109868", product.meta!!.barcode)
    }

    /**
     * The add endpoint echoes back only the fields that were sent. Gson leaves everything else
     * null, so this is the shape most likely to crash the app if the DTO were not fully nullable.
     */
    @Test
    fun `it maps the partial product returned by the add endpoint without blowing up`() {
        val dto = gson.fromJson(ADDED_PRODUCT_JSON, ProductDto::class.java)

        val product = dto.toDomain()

        assertEquals(195, product.id)
        assertEquals("Test Product", product.title)
        assertEquals(99.5, product.price, DELTA)
        assertEquals("beauty", product.category)
        assertEquals(7, product.stock)
        assertNull(product.rating)
        assertNull(product.thumbnail)
        assertNull(product.dimensions)
        assertNull(product.meta)
        assertTrue(product.tags.isEmpty())
        assertTrue(product.images.isEmpty())
        assertTrue(product.reviews.isEmpty())
    }

    @Test
    fun `it survives a payload where every single field is missing`() {
        val dto = gson.fromJson("{}", ProductDto::class.java)

        val product = dto.toDomain()

        assertEquals(0, product.id)
        assertEquals("", product.title)
        assertEquals(0.0, product.price, DELTA)
        assertTrue(product.tags.isEmpty())
        assertTrue(product.images.isEmpty())
        assertTrue(product.reviews.isEmpty())
        assertNull(product.dimensions)
        assertNull(product.meta)
    }

    @Test
    fun `it maps a list envelope into a page`() {
        val response = gson.fromJson(
            """{"products":[$FULL_PRODUCT_JSON],"total":194,"skip":0,"limit":30}""",
            ProductListResponse::class.java
        )

        val page = response.toDomain()

        assertEquals(1, page.products.size)
        assertEquals(194, page.total)
        assertEquals(0, page.skip)
        assertEquals(30, page.limit)
        assertTrue(page.hasMore)
    }

    @Test
    fun `an empty search envelope maps to an empty page`() {
        val response = gson.fromJson(
            """{"products":[],"total":0,"skip":0,"limit":0}""",
            ProductListResponse::class.java
        )

        val page = response.toDomain()

        assertTrue(page.products.isEmpty())
        assertEquals(0, page.total)
    }

    private companion object {
        const val DELTA = 0.001

        const val FULL_PRODUCT_JSON = """
        {
          "id": 1,
          "title": "Essence Mascara Lash Princess",
          "description": "A popular mascara.",
          "category": "beauty",
          "price": 9.99,
          "discountPercentage": 7.17,
          "rating": 4.94,
          "stock": 5,
          "tags": ["beauty", "mascara"],
          "brand": "Essence",
          "sku": "RCH45Q1A",
          "weight": 2,
          "dimensions": { "width": 23.17, "height": 14.43, "depth": 28.01 },
          "warrantyInformation": "1 month warranty",
          "shippingInformation": "Ships in 1 month",
          "availabilityStatus": "Low Stock",
          "reviews": [
            {
              "rating": 2,
              "comment": "Very unhappy with my purchase!",
              "date": "2024-05-23T08:56:21.618Z",
              "reviewerName": "John Doe",
              "reviewerEmail": "john.doe@x.dummyjson.com"
            }
          ],
          "returnPolicy": "30 days return policy",
          "minimumOrderQuantity": 24,
          "meta": {
            "createdAt": "2024-05-23T08:56:21.618Z",
            "updatedAt": "2024-05-23T08:56:21.618Z",
            "barcode": "9164035109868",
            "qrCode": "https://cdn.dummyjson.com/public/qr-code.png"
          },
          "thumbnail": "https://cdn.dummyjson.com/p/1/thumbnail.png",
          "images": ["https://cdn.dummyjson.com/p/1/1.png"]
        }
        """

        const val ADDED_PRODUCT_JSON = """
        {
          "id": 195,
          "title": "Test Product",
          "price": 99.5,
          "stock": 7,
          "description": "desc",
          "brand": "ACME",
          "category": "beauty"
        }
        """
    }
}

package id.my.hizari.dummyjsonpreview.data.product.api

import id.my.hizari.dummyjsonpreview.data.product.model.DeleteProductResponse
import id.my.hizari.dummyjsonpreview.data.product.model.ProductDto
import id.my.hizari.dummyjsonpreview.data.product.model.ProductListResponse
import id.my.hizari.dummyjsonpreview.data.product.model.ProductRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * id.my.hizari.dummyjsonpreview.data.product.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

interface ProductApi {

    @GET(value = "products")
    suspend fun getProducts(
        @Query(value = "limit") limit: Int,
        @Query(value = "skip") skip: Int
    ): ProductListResponse

    @GET(value = "products/search")
    suspend fun searchProducts(
        @Query(value = "q") query: String,
        @Query(value = "limit") limit: Int,
        @Query(value = "skip") skip: Int
    ): ProductListResponse

    @GET(value = "products/{id}")
    suspend fun getProduct(@Path(value = "id") id: Int): ProductDto

    @POST(value = "products/add")
    suspend fun addProduct(@Body request: ProductRequest): ProductDto

    @PUT(value = "products/{id}")
    suspend fun updateProduct(@Path(value = "id") id: Int, @Body request: ProductRequest): ProductDto

    @DELETE(value = "products/{id}")
    suspend fun deleteProduct(@Path(value = "id") id: Int): DeleteProductResponse
}

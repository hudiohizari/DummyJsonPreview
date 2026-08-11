package id.my.hizari.dummyjsonpreview.domain.product.repository

import id.my.hizari.dummyjsonpreview.domain.product.model.DeletedProduct
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductPage

/**
 * id.my.hizari.dummyjsonpreview.domain.product.repository
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

interface ProductRepository {

    suspend fun getProducts(limit: Int, skip: Int): ProductPage

    suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductPage

    suspend fun getProduct(id: Int): Product

    suspend fun addProduct(draft: ProductDraft): Product

    suspend fun updateProduct(id: Int, draft: ProductDraft): Product

    suspend fun deleteProduct(id: Int): DeletedProduct
}

package id.my.hizari.dummyjsonpreview.data.product.repository

import id.my.hizari.dummyjsonpreview.data.product.api.ProductApi
import id.my.hizari.dummyjsonpreview.data.product.mapper.toDomain
import id.my.hizari.dummyjsonpreview.data.product.mapper.toRequest
import id.my.hizari.dummyjsonpreview.data.network.ApiErrorMapper
import id.my.hizari.dummyjsonpreview.domain.product.model.DeletedProduct
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductPage
import id.my.hizari.dummyjsonpreview.domain.product.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.product.repository
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val errorMapper: ApiErrorMapper
) : ProductRepository {

    override suspend fun getProducts(limit: Int, skip: Int): ProductPage = errorMapper.call(block = {
        api.getProducts(limit = limit, skip = skip).toDomain()
    })

    override suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductPage =
        errorMapper.call(block = {
            api.searchProducts(query = query, limit = limit, skip = skip).toDomain()
        })

    override suspend fun getProduct(id: Int): Product = errorMapper.call(block = {
        api.getProduct(id = id).toDomain()
    })

    // DummyJSON simulates the write endpoints: the response is well-formed but nothing is stored,
    // so the mapped result is surfaced as-is and never merged into a cached list.
    override suspend fun addProduct(draft: ProductDraft): Product = errorMapper.call(block = {
        api.addProduct(request = draft.toRequest()).toDomain()
    })

    override suspend fun updateProduct(id: Int, draft: ProductDraft): Product = errorMapper.call(block = {
        api.updateProduct(id = id, request = draft.toRequest()).toDomain()
    })

    override suspend fun deleteProduct(id: Int): DeletedProduct = errorMapper.call(block = {
        api.deleteProduct(id = id).toDomain()
    })
}

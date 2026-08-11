package id.my.hizari.dummyjsonpreview.domain.usecase

import id.my.hizari.dummyjsonpreview.domain.model.ProductPage
import id.my.hizari.dummyjsonpreview.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Folds listing and searching into one call so callers can page through either the full catalogue
 * or a set of search results without branching.
 */
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(
        query: String = "",
        limit: Int = PAGE_SIZE,
        skip: Int = 0
    ): ProductPage {
        val trimmedQuery = query.trim()
        return if (trimmedQuery.isEmpty()) {
            repository.getProducts(limit = limit, skip = skip)
        } else {
            repository.searchProducts(query = trimmedQuery, limit = limit, skip = skip)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}

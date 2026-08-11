package id.my.hizari.dummyjsonpreview.domain.product.usecase

import id.my.hizari.dummyjsonpreview.domain.product.model.DeletedProduct
import id.my.hizari.dummyjsonpreview.domain.product.repository.ProductRepository
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.product.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(id: Int): DeletedProduct = repository.deleteProduct(id = id)
}

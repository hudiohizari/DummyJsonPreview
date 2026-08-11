package id.my.hizari.dummyjsonpreview.domain.usecase

import id.my.hizari.dummyjsonpreview.domain.model.DeletedProduct
import id.my.hizari.dummyjsonpreview.domain.model.Product
import id.my.hizari.dummyjsonpreview.domain.model.ProductDraft
import id.my.hizari.dummyjsonpreview.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class GetProductDetailUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(id: Int): Product = repository.getProduct(id = id)
}

class AddProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(draft: ProductDraft): Product = repository.addProduct(draft = draft)
}

class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(id: Int, draft: ProductDraft): Product =
        repository.updateProduct(id = id, draft = draft)
}

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(id: Int): DeletedProduct = repository.deleteProduct(id = id)
}

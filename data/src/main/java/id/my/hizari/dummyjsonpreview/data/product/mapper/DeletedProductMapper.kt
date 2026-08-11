package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.DeleteProductResponse
import id.my.hizari.dummyjsonpreview.domain.product.model.DeletedProduct

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun DeleteProductResponse.toDomain(): DeletedProduct = DeletedProduct(
    id = id ?: 0,
    title = title,
    isDeleted = isDeleted ?: false,
    deletedOn = deletedOn
)

package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.ProductListResponse
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductPage

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun ProductListResponse.toDomain(): ProductPage = ProductPage(
    products = products?.map(transform = { it.toDomain() }).orEmpty(),
    total = total ?: 0,
    skip = skip ?: 0,
    limit = limit ?: 0
)

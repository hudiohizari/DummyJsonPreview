package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.ProductRequest
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun ProductDraft.toRequest(): ProductRequest = ProductRequest(
    title = title,
    description = description,
    category = category,
    price = price,
    discountPercentage = discountPercentage,
    stock = stock,
    brand = brand
)

package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.ProductDto
import id.my.hizari.dummyjsonpreview.domain.product.model.Product

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun ProductDto.toDomain(): Product = Product(
    id = id ?: 0,
    title = title.orEmpty(),
    description = description,
    category = category,
    price = price ?: 0.0,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    tags = tags.orEmpty(),
    brand = brand,
    sku = sku,
    weight = weight,
    dimensions = dimensions?.toDomain(),
    warrantyInformation = warrantyInformation,
    shippingInformation = shippingInformation,
    availabilityStatus = availabilityStatus,
    reviews = reviews?.map(transform = { it.toDomain() }).orEmpty(),
    returnPolicy = returnPolicy,
    minimumOrderQuantity = minimumOrderQuantity,
    meta = meta?.toDomain(),
    thumbnail = thumbnail,
    images = images.orEmpty()
)

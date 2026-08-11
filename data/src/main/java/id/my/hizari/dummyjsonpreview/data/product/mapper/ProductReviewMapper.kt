package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.ReviewDto
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductReview

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun ReviewDto.toDomain(): ProductReview = ProductReview(
    rating = rating ?: 0,
    comment = comment,
    date = date,
    reviewerName = reviewerName,
    reviewerEmail = reviewerEmail
)

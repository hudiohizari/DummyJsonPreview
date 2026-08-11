package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.DimensionsDto
import id.my.hizari.dummyjsonpreview.domain.product.model.Dimensions

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun DimensionsDto.toDomain(): Dimensions = Dimensions(
    width = width ?: 0.0,
    height = height ?: 0.0,
    depth = depth ?: 0.0
)

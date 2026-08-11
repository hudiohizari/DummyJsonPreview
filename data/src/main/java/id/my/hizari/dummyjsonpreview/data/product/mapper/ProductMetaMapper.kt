package id.my.hizari.dummyjsonpreview.data.product.mapper

import id.my.hizari.dummyjsonpreview.data.product.model.MetaDto
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductMeta

/**
 * id.my.hizari.dummyjsonpreview.data.product.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun MetaDto.toDomain(): ProductMeta = ProductMeta(
    createdAt = createdAt,
    updatedAt = updatedAt,
    barcode = barcode,
    qrCode = qrCode
)

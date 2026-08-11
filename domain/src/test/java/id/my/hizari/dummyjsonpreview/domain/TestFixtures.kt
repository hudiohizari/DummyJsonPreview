package id.my.hizari.dummyjsonpreview.domain

import id.my.hizari.dummyjsonpreview.domain.model.Product
import id.my.hizari.dummyjsonpreview.domain.model.ProductPage
import id.my.hizari.dummyjsonpreview.domain.model.User

/**
 * id.my.hizari.dummyjsonpreview.domain
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun stubUser(
    id: Int = 1,
    firstName: String? = "Emily",
    lastName: String? = "Johnson"
): User = User(
    id = id,
    username = "emilys",
    email = "emily.johnson@x.dummyjson.com",
    firstName = firstName,
    lastName = lastName,
    gender = "female",
    image = "https://dummyjson.com/icon/emilys/128"
)

fun stubProduct(
    id: Int = 1,
    title: String = "Essence Mascara Lash Princess",
    price: Double = 9.99,
    discountPercentage: Double? = null
): Product = Product(
    id = id,
    title = title,
    description = null,
    category = "beauty",
    price = price,
    discountPercentage = discountPercentage,
    rating = null,
    stock = null,
    brand = null,
    sku = null,
    weight = null,
    dimensions = null,
    warrantyInformation = null,
    shippingInformation = null,
    availabilityStatus = null,
    returnPolicy = null,
    minimumOrderQuantity = null,
    meta = null,
    thumbnail = null
)

fun stubProductPage(
    count: Int = 20,
    total: Int = 194,
    skip: Int = 0,
    limit: Int = 20
): ProductPage = ProductPage(
    products = List(size = count) { index -> stubProduct(id = skip + index) },
    total = total,
    skip = skip,
    limit = limit
)

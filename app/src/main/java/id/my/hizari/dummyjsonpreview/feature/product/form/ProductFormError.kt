package id.my.hizari.dummyjsonpreview.feature.product.form

/**
 * id.my.hizari.dummyjsonpreview.feature.product.form
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Why a field was rejected. The form reports the reason rather than a message so the view model
 * stays free of resources and the screen decides the wording.
 */
enum class ProductFormError {
    REQUIRED,
    INVALID_NUMBER,
    PERCENT_RANGE
}

package id.my.hizari.dummyjsonpreview.feature.product.form

import id.my.hizari.dummyjsonpreview.domain.product.model.ProductDraft

/**
 * id.my.hizari.dummyjsonpreview.feature.product.form
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/** Shared by adding and editing, which submit the same fields to different endpoints. */
data class ProductFormState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val price: String = "",
    val discountPercentage: String = "",
    val stock: String = "",
    val brand: String = "",
    val titleError: ProductFormError? = null,
    val priceError: ProductFormError? = null,
    val discountError: ProductFormError? = null,
    val stockError: ProductFormError? = null,
    val isLoading: Boolean = false,
    val loadErrorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val submitErrorMessage: String? = null,
    val savedTitle: String? = null
) {
    val hasErrors: Boolean
        get() = titleError != null ||
            priceError != null ||
            discountError != null ||
            stockError != null

    /** A failure only takes over the screen when there are no fields to fall back on. */
    val showFullScreenError: Boolean
        get() = loadErrorMessage != null && title.isEmpty()
}

/**
 * Returns a copy carrying every field error, so one pass reports all of them at once rather than
 * making the user fix the form one submit at a time. Fields that now pass have their error cleared.
 */
fun ProductFormState.validated(): ProductFormState = copy(
    titleError = if (title.isBlank()) ProductFormError.REQUIRED else null,
    priceError = when {
        price.isBlank() -> ProductFormError.REQUIRED
        // A product priced at nothing is not something this form should be able to submit.
        price.toFormDouble()?.takeIf { it > 0 } == null -> ProductFormError.INVALID_NUMBER
        else -> null
    },
    discountError = discountPercentage.trim().takeIf { it.isNotEmpty() }?.let(block = { value ->
        val percent = value.toFormDouble()
        if (percent == null || percent < 0 || percent > 100) ProductFormError.PERCENT_RANGE else null
    }),
    stockError = stock.trim().takeIf { it.isNotEmpty() }?.let(block = { value ->
        val count = value.toFormInt()
        if (count == null || count < 0) ProductFormError.INVALID_NUMBER else null
    })
)

/**
 * Blank optional fields become null rather than empty values, so editing a product does not
 * overwrite what is already there with blanks.
 */
fun ProductFormState.toDraft(): ProductDraft = ProductDraft(
    title = title.trim(),
    description = description.trim().takeIf { it.isNotEmpty() },
    category = category.trim().takeIf { it.isNotEmpty() },
    price = price.toFormDouble(),
    discountPercentage = discountPercentage.toFormDouble(),
    stock = stock.toFormInt(),
    brand = brand.trim().takeIf { it.isNotEmpty() }
)

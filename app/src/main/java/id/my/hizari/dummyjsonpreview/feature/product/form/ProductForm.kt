package id.my.hizari.dummyjsonpreview.feature.product.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.ui.components.StateBanner
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.product.form
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/** The field set shared by adding and editing, which differ only in what submitting does. */
@Composable
fun ProductForm(
    modifier: Modifier = Modifier,
    state: ProductFormState,
    submitLabel: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        if (state.submitErrorMessage != null) {
            StateBanner(
                icon = Icons.Default.ErrorOutline,
                message = state.submitErrorMessage,
                iconTint = MaterialTheme.colorScheme.onErrorContainer,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                messageColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        Column(modifier = Modifier.padding(all = 16.dp)) {
            FormField(
                value = state.title,
                onValueChange = onTitleChange,
                label = stringResource(id = R.string.label_title),
                error = state.titleError
            )
            FormField(
                value = state.price,
                onValueChange = onPriceChange,
                label = stringResource(id = R.string.label_price),
                error = state.priceError,
                keyboardType = KeyboardType.Decimal
            )
            FormField(
                value = state.category,
                onValueChange = onCategoryChange,
                label = stringResource(id = R.string.label_category),
                isOptional = true
            )
            FormField(
                value = state.brand,
                onValueChange = onBrandChange,
                label = stringResource(id = R.string.label_brand),
                isOptional = true
            )
            FormField(
                value = state.discountPercentage,
                onValueChange = onDiscountChange,
                label = stringResource(id = R.string.label_discount),
                error = state.discountError,
                keyboardType = KeyboardType.Decimal,
                isOptional = true
            )
            FormField(
                value = state.stock,
                onValueChange = onStockChange,
                label = stringResource(id = R.string.label_stock),
                error = state.stockError,
                keyboardType = KeyboardType.Number,
                isOptional = true
            )
            FormField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = stringResource(id = R.string.label_description),
                imeAction = ImeAction.Done,
                isOptional = true,
                singleLine = false
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = onSubmit,
                enabled = !state.isSubmitting,
                content = {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(size = 20.dp))
                    } else {
                        Text(text = submitLabel)
                    }
                }
            )
        }
    }
}

@Composable
private fun FormField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: ProductFormError? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isOptional: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = if (isOptional) {
            { Text(text = stringResource(id = R.string.hint_optional)) }
        } else {
            null
        },
        isError = error != null,
        supportingText = error?.let(block = { { Text(text = stringResource(id = it.messageRes())) } }),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)
    )
}

/** The reason a field was rejected only becomes wording here, at the edge that has resources. */
private fun ProductFormError.messageRes(): Int = when (this) {
    ProductFormError.REQUIRED -> R.string.error_field_required
    ProductFormError.INVALID_NUMBER -> R.string.error_field_number
    ProductFormError.PERCENT_RANGE -> R.string.error_field_percent
}

@Preview(name = "Product form", showBackground = true)
@Composable
private fun ProductFormPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductForm(
            state = ProductFormState(
                title = "Essence Mascara Lash Princess",
                price = "9.99",
                category = "beauty",
                brand = "Essence"
            ),
            submitLabel = "Save",
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Product form invalid", showBackground = true)
@Composable
private fun ProductFormInvalidPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductForm(
            state = ProductFormState(
                price = "abc",
                discountPercentage = "150",
                stock = "-3",
                titleError = ProductFormError.REQUIRED,
                priceError = ProductFormError.INVALID_NUMBER,
                discountError = ProductFormError.PERCENT_RANGE,
                stockError = ProductFormError.INVALID_NUMBER
            ),
            submitLabel = "Save",
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Product form failed save", showBackground = true)
@Composable
private fun ProductFormSubmitErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductForm(
            state = ProductFormState(
                title = "Essence Mascara Lash Princess",
                price = "9.99",
                submitErrorMessage = "Network unavailable"
            ),
            submitLabel = "Save",
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Product form saving", showBackground = true)
@Composable
private fun ProductFormSubmittingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductForm(
            state = ProductFormState(
                title = "Essence Mascara Lash Princess",
                price = "9.99",
                isSubmitting = true
            ),
            submitLabel = "Save",
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onSubmit = {}
        )
    }
}

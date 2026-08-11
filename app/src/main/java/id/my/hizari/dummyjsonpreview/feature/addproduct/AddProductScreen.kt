package id.my.hizari.dummyjsonpreview.feature.addproduct

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductForm
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductFormState
import id.my.hizari.dummyjsonpreview.ui.components.MessageDialog
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.addproduct
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    viewModel: AddProductViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddProductContent(
        modifier = modifier,
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategoryChange = viewModel::onCategoryChange,
        onPriceChange = viewModel::onPriceChange,
        onDiscountChange = viewModel::onDiscountChange,
        onStockChange = viewModel::onStockChange,
        onBrandChange = viewModel::onBrandChange,
        onSubmit = viewModel::onSubmit,
        onSavedAcknowledged = viewModel::onSavedAcknowledged
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductContent(
    modifier: Modifier = Modifier,
    state: ProductFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSavedAcknowledged: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.title_add_product)) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            ProductForm(
                state = state,
                submitLabel = stringResource(id = R.string.action_save),
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onCategoryChange = onCategoryChange,
                onPriceChange = onPriceChange,
                onDiscountChange = onDiscountChange,
                onStockChange = onStockChange,
                onBrandChange = onBrandChange,
                onSubmit = onSubmit
            )
        }
    }

    // There is no screen to leave, so acknowledging clears the form for the next product.
    if (state.savedTitle != null) {
        MessageDialog(
            title = stringResource(id = R.string.save_success_title),
            message = stringResource(id = R.string.save_success_message, state.savedTitle),
            onDismiss = onSavedAcknowledged
        )
    }
}

@Preview(name = "Add product", showBackground = true)
@Composable
private fun AddProductPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        AddProductContent(
            state = ProductFormState(),
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onSubmit = {},
            onSavedAcknowledged = {}
        )
    }
}

@Preview(name = "Add product saved", showBackground = true)
@Composable
private fun AddProductSavedPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        AddProductContent(
            state = ProductFormState(
                title = "Essence Mascara Lash Princess",
                price = "9.99",
                savedTitle = "Essence Mascara Lash Princess"
            ),
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onSubmit = {},
            onSavedAcknowledged = {}
        )
    }
}

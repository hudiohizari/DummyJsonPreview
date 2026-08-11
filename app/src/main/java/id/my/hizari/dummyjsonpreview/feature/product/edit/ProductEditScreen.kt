package id.my.hizari.dummyjsonpreview.feature.product.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductForm
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductFormState
import id.my.hizari.dummyjsonpreview.ui.components.MessageDialog
import id.my.hizari.dummyjsonpreview.ui.components.StateMessage
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.product.edit
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProductEditScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductEditViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProductEditContent(
        modifier = modifier,
        state = state,
        onNavigateBack = onNavigateBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCategoryChange = viewModel::onCategoryChange,
        onPriceChange = viewModel::onPriceChange,
        onDiscountChange = viewModel::onDiscountChange,
        onStockChange = viewModel::onStockChange,
        onBrandChange = viewModel::onBrandChange,
        onRetry = viewModel::onRetry,
        onSubmit = viewModel::onSubmit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditContent(
    modifier: Modifier = Modifier,
    state: ProductFormState,
    onNavigateBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onRetry: () -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.title_edit_product)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.action_back)
                            )
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(alignment = Alignment.Center)
                )

                state.showFullScreenError -> StateMessage(
                    icon = Icons.Default.ErrorOutline,
                    title = stringResource(id = R.string.error_title),
                    message = state.loadErrorMessage.orEmpty(),
                    iconTint = MaterialTheme.colorScheme.error,
                    actionLabel = stringResource(id = R.string.action_retry),
                    onAction = onRetry
                )

                else -> ProductForm(
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
    }

    if (state.savedTitle != null) {
        MessageDialog(
            title = stringResource(id = R.string.save_success_title),
            message = stringResource(id = R.string.save_success_message, state.savedTitle),
            onDismiss = onNavigateBack
        )
    }
}

@Preview(name = "Product edit", showBackground = true)
@Composable
private fun ProductEditPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductEditContent(
            state = previewState(),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onRetry = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Product edit loading", showBackground = true)
@Composable
private fun ProductEditLoadingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductEditContent(
            state = ProductFormState(isLoading = true),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onRetry = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Product edit load failed", showBackground = true)
@Composable
private fun ProductEditErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductEditContent(
            state = ProductFormState(loadErrorMessage = "Network unavailable"),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onRetry = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Product edit saved", showBackground = true)
@Composable
private fun ProductEditSavedPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductEditContent(
            state = previewState().copy(savedTitle = "Essence Mascara Lash Princess"),
            onNavigateBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onCategoryChange = {},
            onPriceChange = {},
            onDiscountChange = {},
            onStockChange = {},
            onBrandChange = {},
            onRetry = {},
            onSubmit = {}
        )
    }
}

private fun previewState() = ProductFormState(
    title = "Essence Mascara Lash Princess",
    description = "A popular mascara.",
    category = "beauty",
    price = "9.99",
    discountPercentage = "7.17",
    stock = "99",
    brand = "Essence"
)

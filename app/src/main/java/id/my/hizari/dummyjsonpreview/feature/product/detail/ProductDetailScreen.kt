package id.my.hizari.dummyjsonpreview.feature.product.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.domain.product.model.Dimensions
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.model.ProductReview
import id.my.hizari.dummyjsonpreview.ui.components.MessageDialog
import id.my.hizari.dummyjsonpreview.ui.components.StateMessage
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme
import id.my.hizari.dummyjsonpreview.util.toDecimalLabel
import id.my.hizari.dummyjsonpreview.util.toPercentLabel
import id.my.hizari.dummyjsonpreview.util.toPriceLabel

/**
 * id.my.hizari.dummyjsonpreview.feature.product.detail
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onEditClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProductDetailContent(
        modifier = modifier,
        state = state,
        onNavigateBack = onNavigateBack,
        onEditClick = onEditClick,
        onRetry = viewModel::onRetry,
        onDeleteClick = viewModel::onDeleteClick,
        onDeleteDismiss = viewModel::onDeleteDismiss,
        onDeleteConfirm = viewModel::onDeleteConfirm
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    modifier: Modifier = Modifier,
    state: ProductDetailState,
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.product?.title ?: stringResource(id = R.string.title_products),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
                },
                actions = {
                    // Both actions need a loaded product, so they stay hidden until there is one.
                    if (state.product != null) {
                        IconButton(
                            onClick = { onEditClick(state.product.id) },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(id = R.string.action_edit)
                                )
                            }
                        )
                        IconButton(
                            onClick = onDeleteClick,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.action_delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
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
                    message = state.errorMessage.orEmpty(),
                    iconTint = MaterialTheme.colorScheme.error,
                    actionLabel = stringResource(id = R.string.action_retry),
                    onAction = onRetry
                )

                state.product != null -> ProductDetailBody(product = state.product)
            }
        }
    }

    if (state.isDeleteDialogVisible) {
        DeleteConfirmDialog(
            productTitle = state.product?.title.orEmpty(),
            isDeleting = state.isDeleting,
            errorMessage = state.deleteErrorMessage,
            onDismiss = onDeleteDismiss,
            onConfirm = onDeleteConfirm
        )
    }

    // The product no longer exists, so acknowledging the result leaves the screen.
    if (state.deletedTitle != null) {
        MessageDialog(
            title = stringResource(id = R.string.delete_success_title),
            message = stringResource(id = R.string.delete_success_message, state.deletedTitle),
            onDismiss = onNavigateBack
        )
    }
}

@Composable
private fun ProductDetailBody(
    modifier: Modifier = Modifier,
    product: Product
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        ProductGallery(product = product)

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(text = product.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = listOfNotNull(product.brand, product.category)
                    .joinToString(separator = " - "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ProductPrice(modifier = Modifier.padding(top = 12.dp), product = product)
            ProductFacts(modifier = Modifier.padding(top = 8.dp), product = product)

            if (product.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    product.tags.forEach { tag ->
                        AssistChip(onClick = {}, label = { Text(text = tag) })
                    }
                }
            }

            val description = product.description
            if (!description.isNullOrBlank()) {
                SectionTitle(title = stringResource(id = R.string.section_description))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionTitle(title = stringResource(id = R.string.section_specifications))
            SpecRow(label = stringResource(id = R.string.label_brand), value = product.brand)
            SpecRow(label = stringResource(id = R.string.label_sku), value = product.sku)
            SpecRow(
                label = stringResource(id = R.string.label_weight),
                value = product.weight?.let(block = {
                    stringResource(id = R.string.format_weight, it.toDecimalLabel())
                })
            )
            SpecRow(
                label = stringResource(id = R.string.label_dimensions),
                value = product.dimensions?.let(block = {
                    stringResource(
                        id = R.string.format_dimensions,
                        it.width.toDecimalLabel(),
                        it.height.toDecimalLabel(),
                        it.depth.toDecimalLabel()
                    )
                })
            )
            SpecRow(
                label = stringResource(id = R.string.label_warranty),
                value = product.warrantyInformation
            )
            SpecRow(
                label = stringResource(id = R.string.label_shipping),
                value = product.shippingInformation
            )
            SpecRow(
                label = stringResource(id = R.string.label_return_policy),
                value = product.returnPolicy
            )
            SpecRow(
                label = stringResource(id = R.string.label_minimum_order),
                value = product.minimumOrderQuantity?.let(block = {
                    stringResource(id = R.string.format_minimum_order, it)
                })
            )

            SectionTitle(title = stringResource(id = R.string.section_reviews))
            if (product.reviews.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.empty_reviews_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                product.reviews.forEach { review -> ReviewItem(review = review) }
            }
        }
    }
}

@Composable
private fun ProductGallery(
    modifier: Modifier = Modifier,
    product: Product
) {
    // Falling back to the thumbnail keeps the layout stable for products with no gallery.
    val images = product.images.ifEmpty { listOfNotNull(product.thumbnail) }
    if (images.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {
        items(items = images) { image ->
            AsyncImage(
                modifier = Modifier
                    .width(width = 260.dp)
                    .height(height = 240.dp)
                    .clip(shape = RoundedCornerShape(size = 12.dp)),
                model = image,
                contentDescription = product.title,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ProductPrice(
    modifier: Modifier = Modifier,
    product: Product
) {
    val discount = product.discountPercentage
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = product.discountedPrice.toPriceLabel(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        if (discount != null && discount > 0) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = product.price.toPriceLabel(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.format_discount, discount.toPercentLabel()),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ProductFacts(
    modifier: Modifier = Modifier,
    product: Product
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        product.rating?.let(block = { rating ->
            Icon(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(size = 18.dp),
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = rating.toDecimalLabel(), style = MaterialTheme.typography.bodyMedium)
        })
        product.stock?.let(block = { stock ->
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = stringResource(id = R.string.format_stock, stock),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        })
        product.availabilityStatus?.let(block = { status ->
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        })
    }
}

@Composable
private fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        modifier = modifier.padding(top = 20.dp, bottom = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleMedium
    )
}

/** Skips itself when the field is missing, so absent data leaves no empty row behind. */
@Composable
private fun SpecRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String?
) {
    if (value.isNullOrBlank()) return

    Row(modifier = modifier.padding(vertical = 4.dp)) {
        Text(
            modifier = Modifier.width(width = 120.dp),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            modifier = Modifier.weight(weight = 1f),
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ReviewItem(
    modifier: Modifier = Modifier,
    review: ProductReview
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = review.reviewerName.orEmpty(),
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(size = 16.dp),
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = review.rating.toString(), style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = review.comment.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun DeleteConfirmDialog(
    productTitle: String,
    isDeleting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        // Dismissing mid-request would leave the call running with nothing to report back to.
        onDismissRequest = { if (!isDeleting) onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(text = stringResource(id = R.string.delete_confirm_title)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.delete_confirm_message, productTitle))
                if (errorMessage != null) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isDeleting,
                content = {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(size = 20.dp))
                    } else {
                        Text(
                            text = stringResource(id = R.string.action_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting,
                content = { Text(text = stringResource(id = R.string.action_cancel)) }
            )
        }
    )
}

@Preview(name = "Product detail", showBackground = true)
@Composable
private fun ProductDetailPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(product = previewProduct()),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(name = "Product detail loading", showBackground = true)
@Composable
private fun ProductDetailLoadingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(isLoading = true),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(name = "Product detail error", showBackground = true)
@Composable
private fun ProductDetailErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(errorMessage = "Network unavailable"),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(name = "Product detail delete confirm", showBackground = true)
@Composable
private fun ProductDetailDeleteConfirmPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(
                product = previewProduct(),
                isDeleteDialogVisible = true
            ),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(name = "Product detail deleting", showBackground = true)
@Composable
private fun ProductDetailDeletingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(
                product = previewProduct(),
                isDeleteDialogVisible = true,
                isDeleting = true
            ),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(name = "Product detail delete failed", showBackground = true)
@Composable
private fun ProductDetailDeleteErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(
                product = previewProduct(),
                isDeleteDialogVisible = true,
                deleteErrorMessage = "Network unavailable"
            ),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(name = "Product detail deleted", showBackground = true)
@Composable
private fun ProductDetailDeletedPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductDetailContent(
            state = ProductDetailState(
                product = previewProduct(),
                deletedTitle = "Essence Mascara Lash Princess"
            ),
            onNavigateBack = {},
            onEditClick = {},
            onRetry = {},
            onDeleteClick = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

private fun previewProduct() = Product(
    id = 1,
    title = "Essence Mascara Lash Princess",
    description = "The Essence Mascara Lash Princess is a popular mascara known for its " +
        "volumizing and lengthening effects.",
    category = "beauty",
    price = 9.99,
    discountPercentage = 7.17,
    rating = 4.94,
    stock = 5,
    tags = listOf("beauty", "mascara"),
    brand = "Essence",
    sku = "RCH45Q1A",
    weight = 2.0,
    dimensions = Dimensions(width = 23.17, height = 14.43, depth = 28.01),
    warrantyInformation = "1 month warranty",
    shippingInformation = "Ships in 1 month",
    availabilityStatus = "Low Stock",
    reviews = listOf(
        ProductReview(
            rating = 3,
            comment = "Would not recommend!",
            date = "2025-04-30T09:41:02.053Z",
            reviewerName = "Eleanor Collins",
            reviewerEmail = "eleanor.collins@x.dummyjson.com"
        )
    ),
    returnPolicy = "30 days return policy",
    minimumOrderQuantity = 24,
    meta = null,
    thumbnail = null
)

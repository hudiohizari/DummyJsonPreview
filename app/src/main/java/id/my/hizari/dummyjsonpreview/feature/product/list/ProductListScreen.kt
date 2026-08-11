package id.my.hizari.dummyjsonpreview.feature.product.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.ui.components.LoadingIndicator
import id.my.hizari.dummyjsonpreview.ui.components.StateBanner
import id.my.hizari.dummyjsonpreview.ui.components.StateMessage
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.product.list
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProductListContent(
        modifier = modifier,
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::onClearQuery,
        onLoadNextPage = viewModel::onLoadNextPage,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry,
        onProductClick = onProductClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListContent(
    modifier: Modifier = Modifier,
    state: ProductListState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onLoadNextPage: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = stringResource(id = R.string.greeting_welcome, state.greetingName),
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            value = state.query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = stringResource(id = R.string.hint_search_products)) },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearQuery,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.action_clear_search)
                            )
                        }
                    )
                }
            }
        )

        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {
            when {
                state.isLoading -> LoadingIndicator()

                state.showFullScreenError -> StateMessage(
                    icon = Icons.Default.ErrorOutline,
                    title = stringResource(id = R.string.error_title),
                    message = state.errorMessage.orEmpty(),
                    iconTint = MaterialTheme.colorScheme.error,
                    actionLabel = stringResource(id = R.string.action_retry),
                    onAction = onRetry
                )

                state.isEmpty -> StateMessage(
                    title = stringResource(id = R.string.empty_products_title),
                    message = stringResource(id = R.string.empty_products_message),
                    icon = Icons.Default.SearchOff
                )

                else -> Column(modifier = Modifier.fillMaxSize()) {
                    // A refresh failed but the previous products are still usable, so the error
                    // sits above them rather than replacing them.
                    if (state.errorMessage != null) {
                        StateBanner(
                            icon = Icons.Default.ErrorOutline,
                            message = state.errorMessage,
                            iconTint = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            messageColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    ProductList(
                        state = state,
                        onLoadNextPage = onLoadNextPage,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductList(
    modifier: Modifier = Modifier,
    state: ProductListState,
    onLoadNextPage: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    // Prefetch a few rows before the end so scrolling does not stall at the boundary.
    val shouldLoadMore by remember(listState) {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            last >= listState.layoutInfo.totalItemsCount - 1 - PREFETCH_DISTANCE
        }
    }
    // Pausing the automatic trigger while a page has failed stops it hammering a failing endpoint,
    // and clearing the error on retry lets it resume on its own.
    val canAutoLoad = state.paginationErrorMessage == null
    LaunchedEffect(key1 = shouldLoadMore, key2 = canAutoLoad) {
        if (shouldLoadMore && canAutoLoad) onLoadNextPage()
    }

    LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
        items(items = state.products, key = { product -> product.id }) { product ->
            ProductListItem(product = product, onClick = { onProductClick(product.id) })
        }
        if (state.isLoadingNextPage) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    contentAlignment = Alignment.Center,
                    content = { CircularProgressIndicator() }
                )
            }
        }
        if (state.paginationErrorMessage != null) {
            item {
                StateBanner(
                    icon = Icons.Default.ErrorOutline,
                    message = state.paginationErrorMessage,
                    actionLabel = stringResource(id = R.string.action_retry),
                    onAction = onLoadNextPage
                )
            }
        }
    }
}

private const val PREFETCH_DISTANCE = 4

@Preview(name = "Product list", showBackground = true)
@Composable
private fun ProductListContentPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(
                greetingName = "Emily Johnson",
                products = List(size = 5, init = { index -> previewProduct(id = index) }),
                total = 194,
                hasMore = true
            ),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

@Preview(name = "Product list loading", showBackground = true)
@Composable
private fun ProductListLoadingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(greetingName = "Emily Johnson", isLoading = true),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

@Preview(name = "Product list empty", showBackground = true)
@Composable
private fun ProductListEmptyPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(greetingName = "Emily Johnson", query = "zzzz"),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

@Preview(name = "Product list refreshing", showBackground = true)
@Composable
private fun ProductListRefreshingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(
                greetingName = "Emily Johnson",
                products = List(size = 5, init = { index -> previewProduct(id = index) }),
                total = 194,
                hasMore = true,
                isRefreshing = true
            ),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

/** A refresh failed while products were on screen, so the error is a banner, not a takeover. */
@Preview(name = "Product list error with data", showBackground = true)
@Composable
private fun ProductListErrorWithDataPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(
                greetingName = "Emily Johnson",
                products = List(size = 5, init = { index -> previewProduct(id = index) }),
                total = 194,
                hasMore = true,
                errorMessage = "Network unavailable"
            ),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

@Preview(name = "Product list paging", showBackground = true)
@Composable
private fun ProductListPagingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(
                greetingName = "Emily Johnson",
                products = List(size = 5, init = { index -> previewProduct(id = index) }),
                total = 194,
                hasMore = true,
                isLoadingNextPage = true
            ),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

@Preview(name = "Product list paging failed", showBackground = true)
@Composable
private fun ProductListPagingErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(
                greetingName = "Emily Johnson",
                products = List(size = 5, init = { index -> previewProduct(id = index) }),
                total = 194,
                hasMore = true,
                paginationErrorMessage = "Network unavailable"
            ),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

@Preview(name = "Product list error", showBackground = true)
@Composable
private fun ProductListErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProductListContent(
            state = ProductListState(
                greetingName = "Emily Johnson",
                errorMessage = "Network unavailable"
            ),
            onQueryChange = {},
            onClearQuery = {},
            onLoadNextPage = {},
            onRefresh = {},
            onRetry = {},
            onProductClick = {}
        )
    }
}

private fun previewProduct(id: Int) = Product(
    id = id,
    title = "Essence Mascara Lash Princess",
    description = null,
    category = "beauty",
    price = 9.99,
    discountPercentage = null,
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
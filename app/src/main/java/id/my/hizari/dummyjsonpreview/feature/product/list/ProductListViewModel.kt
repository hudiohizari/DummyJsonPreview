package id.my.hizari.dummyjsonpreview.feature.product.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * id.my.hizari.dummyjsonpreview.feature.product.list
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@OptIn(FlowPreview::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow(value = "")

    init {
        viewModelScope.launch(block = {
            observeCurrentUserUseCase().collect(collector = { user ->
                _state.update(function = { it.copy(greetingName = user?.fullName.orEmpty()) })
            })
        })
        viewModelScope.launch(block = {
            queryFlow
                // A blank query is the initial load or a cleared field, so it should not wait.
                .debounce(timeout = { query ->
                    if (query.isBlank()) Duration.ZERO else DEBOUNCE_MILLIS.milliseconds
                })
                .distinctUntilChanged()
                .collectLatest(action = { query -> loadFirstPage(query = query) })
        })
    }

    fun onQueryChange(query: String) {
        _state.update(function = { it.copy(query = query) })
        queryFlow.value = query
    }

    fun onClearQuery() {
        onQueryChange(query = "")
    }

    fun onLoadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingNextPage || !current.hasMore) return

        _state.update(function = { it.copy(isLoadingNextPage = true, paginationErrorMessage = null) })
        viewModelScope.launch(block = {
            try {
                // skip follows what is already on screen, so the same call works for a search too.
                val page = getProductsUseCase(
                    query = current.query,
                    limit = GetProductsUseCase.PAGE_SIZE,
                    skip = current.products.size
                )
                _state.update(function = {
                    val merged = it.products + page.products
                    it.copy(
                        products = merged,
                        total = page.total,
                        hasMore = page.products.isNotEmpty() && merged.size < page.total,
                        isLoadingNextPage = false
                    )
                })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isLoadingNextPage = false, paginationErrorMessage = throwable.message)
                })
            }
        })
    }

    /**
     * Unlike the first load this keeps the current products on screen, so a pull to refresh does
     * not flash an empty list and a failure leaves the user with something to look at.
     */
    fun onRefresh() {
        val current = _state.value
        if (current.isRefreshing) return

        _state.update(function = {
            it.copy(isRefreshing = true, errorMessage = null, paginationErrorMessage = null)
        })
        viewModelScope.launch(block = {
            try {
                val page = getProductsUseCase(
                    query = current.query,
                    limit = GetProductsUseCase.PAGE_SIZE,
                    skip = 0
                )
                _state.update(function = {
                    it.copy(
                        products = page.products,
                        total = page.total,
                        hasMore = page.hasMore,
                        isRefreshing = false
                    )
                })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isRefreshing = false, errorMessage = throwable.message)
                })
            }
        })
    }

    fun onRetry() {
        loadFirstPageAsync(query = _state.value.query)
    }

    private fun loadFirstPageAsync(query: String) {
        viewModelScope.launch(block = { loadFirstPage(query = query) })
    }

    private suspend fun loadFirstPage(query: String) {
        _state.update(function = {
            it.copy(
                isLoading = true,
                products = emptyList(),
                hasMore = false,
                errorMessage = null,
                paginationErrorMessage = null
            )
        })
        try {
            val page = getProductsUseCase(
                query = query,
                limit = GetProductsUseCase.PAGE_SIZE,
                skip = 0
            )
            _state.update(function = {
                it.copy(
                    products = page.products,
                    total = page.total,
                    hasMore = page.hasMore,
                    isLoading = false
                )
            })
        } catch (throwable: Throwable) {
            _state.update(function = { it.copy(isLoading = false, errorMessage = throwable.message) })
        }
    }

    companion object {
        const val DEBOUNCE_MILLIS = 500L
    }
}

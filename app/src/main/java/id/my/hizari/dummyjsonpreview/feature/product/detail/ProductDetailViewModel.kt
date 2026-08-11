package id.my.hizari.dummyjsonpreview.feature.product.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.product.usecase.DeleteProductUseCase
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductDetailUseCase
import id.my.hizari.dummyjsonpreview.navigation.NavigationArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.feature.product.detail
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val productId: Int = savedStateHandle[NavigationArgs.PRODUCT_ID] ?: 0

    private val _state = MutableStateFlow(value = ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    init {
        load()
    }

    fun onRetry() {
        load()
    }

    fun onDeleteClick() {
        _state.update(function = { it.copy(isDeleteDialogVisible = true, deleteErrorMessage = null) })
    }

    fun onDeleteDismiss() {
        _state.update(function = {
            it.copy(isDeleteDialogVisible = false, deleteErrorMessage = null)
        })
    }

    fun onDeleteConfirm() {
        if (_state.value.isDeleting) return

        _state.update(function = { it.copy(isDeleting = true, deleteErrorMessage = null) })
        viewModelScope.launch(block = {
            try {
                val deleted = deleteProductUseCase(id = productId)
                _state.update(function = {
                    it.copy(
                        isDeleting = false,
                        isDeleteDialogVisible = false,
                        // The API echoes the title back, but fall back to what is already on
                        // screen so the confirmation never reads as an empty name.
                        deletedTitle = deleted.title ?: it.product?.title.orEmpty()
                    )
                })
            } catch (throwable: Throwable) {
                // The product is still on screen, so the failure stays inside the dialog.
                _state.update(function = {
                    it.copy(isDeleting = false, deleteErrorMessage = throwable.message)
                })
            }
        })
    }

    private fun load() {
        _state.update(function = { it.copy(isLoading = true, errorMessage = null) })
        viewModelScope.launch(block = {
            try {
                val product = getProductDetailUseCase(id = productId)
                _state.update(function = { it.copy(product = product, isLoading = false) })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isLoading = false, errorMessage = throwable.message)
                })
            }
        })
    }
}

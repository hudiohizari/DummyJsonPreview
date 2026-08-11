package id.my.hizari.dummyjsonpreview.feature.product.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.product.model.Product
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductDetailUseCase
import id.my.hizari.dummyjsonpreview.domain.product.usecase.UpdateProductUseCase
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductFormState
import id.my.hizari.dummyjsonpreview.feature.product.form.filteredAsDecimal
import id.my.hizari.dummyjsonpreview.feature.product.form.filteredAsInteger
import id.my.hizari.dummyjsonpreview.feature.product.form.toDraft
import id.my.hizari.dummyjsonpreview.feature.product.form.validated
import id.my.hizari.dummyjsonpreview.navigation.NavigationArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.feature.product.edit
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class ProductEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val updateProductUseCase: UpdateProductUseCase
) : ViewModel() {

    private val productId: Int = savedStateHandle[NavigationArgs.PRODUCT_ID] ?: 0

    private val _state = MutableStateFlow(value = ProductFormState())
    val state: StateFlow<ProductFormState> = _state.asStateFlow()

    init {
        load()
    }

    fun onTitleChange(title: String) {
        _state.update(function = { it.copy(title = title, titleError = null) })
    }

    fun onDescriptionChange(description: String) {
        _state.update(function = { it.copy(description = description) })
    }

    fun onCategoryChange(category: String) {
        _state.update(function = { it.copy(category = category) })
    }

    fun onPriceChange(price: String) {
        _state.update(function = { it.copy(price = price.filteredAsDecimal(), priceError = null) })
    }

    fun onDiscountChange(discountPercentage: String) {
        _state.update(function = {
            it.copy(
                discountPercentage = discountPercentage.filteredAsDecimal(),
                discountError = null
            )
        })
    }

    fun onStockChange(stock: String) {
        _state.update(function = { it.copy(stock = stock.filteredAsInteger(), stockError = null) })
    }

    fun onBrandChange(brand: String) {
        _state.update(function = { it.copy(brand = brand) })
    }

    fun onRetry() {
        load()
    }

    fun onSubmit() {
        if (_state.value.isSubmitting) return

        val validated = _state.value.validated()
        _state.value = validated
        if (validated.hasErrors) return

        _state.update(function = { it.copy(isSubmitting = true, submitErrorMessage = null) })
        viewModelScope.launch(block = {
            try {
                val updated = updateProductUseCase(id = productId, draft = validated.toDraft())
                _state.update(function = {
                    it.copy(isSubmitting = false, savedTitle = updated.title)
                })
            } catch (throwable: Throwable) {
                // A failed update is not a failed load, so the form stays with its values.
                _state.update(function = {
                    it.copy(isSubmitting = false, submitErrorMessage = throwable.message)
                })
            }
        })
    }

    private fun load() {
        _state.update(function = { it.copy(isLoading = true, loadErrorMessage = null) })
        viewModelScope.launch(block = {
            try {
                val product = getProductDetailUseCase(id = productId)
                _state.update(function = { it.filledFrom(product = product) })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isLoading = false, loadErrorMessage = throwable.message)
                })
            }
        })
    }
}

/**
 * Numbers become the text the fields edit. This uses toString rather than a display formatter so
 * an untouched field submits back the value it was loaded with.
 */
private fun ProductFormState.filledFrom(product: Product) = copy(
    title = product.title,
    description = product.description.orEmpty(),
    category = product.category.orEmpty(),
    price = product.price.toString(),
    discountPercentage = product.discountPercentage?.toString().orEmpty(),
    stock = product.stock?.toString().orEmpty(),
    brand = product.brand.orEmpty(),
    isLoading = false
)

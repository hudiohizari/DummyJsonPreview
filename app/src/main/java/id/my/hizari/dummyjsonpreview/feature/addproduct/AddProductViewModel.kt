package id.my.hizari.dummyjsonpreview.feature.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.product.usecase.AddProductUseCase
import id.my.hizari.dummyjsonpreview.feature.product.form.ProductFormState
import id.my.hizari.dummyjsonpreview.feature.product.form.filteredAsDecimal
import id.my.hizari.dummyjsonpreview.feature.product.form.filteredAsInteger
import id.my.hizari.dummyjsonpreview.feature.product.form.toDraft
import id.my.hizari.dummyjsonpreview.feature.product.form.validated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.feature.addproduct
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = ProductFormState())
    val state: StateFlow<ProductFormState> = _state.asStateFlow()

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

    /** Adding is a tab with nothing to go back to, so the form clears for the next product. */
    fun onSavedAcknowledged() {
        _state.value = ProductFormState()
    }

    fun onSubmit() {
        if (_state.value.isSubmitting) return

        val validated = _state.value.validated()
        _state.value = validated
        if (validated.hasErrors) return

        _state.update(function = { it.copy(isSubmitting = true, submitErrorMessage = null) })
        viewModelScope.launch(block = {
            try {
                val created = addProductUseCase(draft = validated.toDraft())
                _state.update(function = {
                    it.copy(isSubmitting = false, savedTitle = created.title)
                })
            } catch (throwable: Throwable) {
                // The typed values stay put so the user can retry without filling the form again.
                _state.update(function = {
                    it.copy(isSubmitting = false, submitErrorMessage = throwable.message)
                })
            }
        })
    }
}

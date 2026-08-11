package id.my.hizari.dummyjsonpreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.product.usecase.GetProductsUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LoginUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _status = MutableStateFlow(value = "Connecting...")
    val status: StateFlow<String> = _status.asStateFlow()

    init {
        viewModelScope.launch(block = {
            _status.value = try {
                val user = loginUseCase(username = "emilys", password = "emilyspass")
                val page = getProductsUseCase(query = "", limit = 20, skip = 0)
                val storedUser = observeCurrentUserUseCase().first()

                buildString(builderAction = {
                    appendLine(value = "Welcome, ${user.fullName}")
                    appendLine(value = "Session stored: ${storedUser != null}")
                    appendLine(value = "${page.total} products, showing ${page.products.size}")
                    appendLine(value = "First: ${page.products.firstOrNull()?.title}")
                })
            } catch (throwable: Throwable) {
                "Failed: ${throwable::class.simpleName} - ${throwable.message}"
            }
        })
    }
}

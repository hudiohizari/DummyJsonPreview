package id.my.hizari.dummyjsonpreview.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.feature.login
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // A successful sign in needs no navigation call: storing the session moves the root graph.
    fun signIn(username: String, password: String) {
        _state.update(function = { it.copy(isLoading = true, errorMessage = null) })
        viewModelScope.launch(block = {
            try {
                loginUseCase(username = username, password = password)
                _state.update(function = { it.copy(isLoading = false) })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isLoading = false, errorMessage = throwable.message)
                })
            }
        })
    }
}

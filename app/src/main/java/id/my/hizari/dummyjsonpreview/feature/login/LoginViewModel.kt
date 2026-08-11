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

    // Prefilled with the DummyJSON demo account so the app can be signed into without typing.
    private val _state = MutableStateFlow(
        value = LoginState(username = DEMO_USERNAME, password = DEMO_PASSWORD)
    )
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onUsernameChange(username: String) {
        _state.update(function = { it.copy(username = username, isUsernameBlank = false) })
    }

    fun onPasswordChange(password: String) {
        _state.update(function = { it.copy(password = password, isPasswordBlank = false) })
    }

    // A successful sign in needs no navigation call: storing the session moves the root graph.
    fun signIn() {
        val current = _state.value
        val usernameBlank = current.username.isBlank()
        val passwordBlank = current.password.isBlank()
        if (usernameBlank || passwordBlank) {
            _state.update(function = {
                it.copy(
                    isUsernameBlank = usernameBlank,
                    isPasswordBlank = passwordBlank,
                    errorMessage = null
                )
            })
            return
        }

        _state.update(function = { it.copy(isLoading = true, errorMessage = null) })
        viewModelScope.launch(block = {
            try {
                loginUseCase(username = current.username, password = current.password)
                _state.update(function = { it.copy(isLoading = false) })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isLoading = false, errorMessage = throwable.message)
                })
            }
        })
    }

    private companion object {
        const val DEMO_USERNAME = "emilys"
        const val DEMO_PASSWORD = "emilyspass"
    }
}

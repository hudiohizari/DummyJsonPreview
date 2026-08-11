package id.my.hizari.dummyjsonpreview.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.LogoutUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.ObserveCurrentUserUseCase
import id.my.hizari.dummyjsonpreview.domain.auth.usecase.RefreshProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.feature.profile
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val refreshProfileUseCase: RefreshProfileUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(value = ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        viewModelScope.launch(block = {
            // The stored user is the source of truth, so a refresh lands here rather than being
            // assigned directly.
            observeCurrentUserUseCase().collect(collector = { user ->
                _state.update(function = { it.copy(user = user) })
            })
        })
    }

    fun onRefresh() {
        if (_state.value.isRefreshing) return

        _state.update(function = { it.copy(isRefreshing = true, errorMessage = null) })
        viewModelScope.launch(block = {
            try {
                refreshProfileUseCase()
                _state.update(function = { it.copy(isRefreshing = false) })
            } catch (throwable: Throwable) {
                _state.update(function = {
                    it.copy(isRefreshing = false, errorMessage = throwable.message)
                })
            }
        })
    }

    fun onLogoutClick() {
        _state.update(function = { it.copy(isLogoutDialogVisible = true) })
    }

    fun onLogoutDismiss() {
        _state.update(function = { it.copy(isLogoutDialogVisible = false) })
    }

    // Clearing the session is enough to move the user: the root graph follows the session state.
    fun onLogoutConfirm() {
        if (_state.value.isLoggingOut) return

        _state.update(function = { it.copy(isLoggingOut = true) })
        viewModelScope.launch(block = {
            logoutUseCase()
            _state.update(function = {
                it.copy(isLoggingOut = false, isLogoutDialogVisible = false)
            })
        })
    }
}

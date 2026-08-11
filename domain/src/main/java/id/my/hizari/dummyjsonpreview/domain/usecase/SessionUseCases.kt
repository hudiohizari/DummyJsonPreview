package id.my.hizari.dummyjsonpreview.domain.usecase

import id.my.hizari.dummyjsonpreview.domain.model.User
import id.my.hizari.dummyjsonpreview.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() {
        repository.logout()
    }
}

class HasActiveSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): Boolean = repository.hasActiveSession()
}

class ObserveCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    operator fun invoke(): Flow<User?> = repository.observeCurrentUser()
}

class RefreshProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): User = repository.refreshProfile()
}

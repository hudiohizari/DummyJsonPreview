package id.my.hizari.dummyjsonpreview.domain.auth.usecase

import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.auth.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class ObserveCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    operator fun invoke(): Flow<User?> = repository.observeCurrentUser()
}

package id.my.hizari.dummyjsonpreview.domain.auth.usecase

import id.my.hizari.dummyjsonpreview.domain.auth.repository.AuthRepository
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.auth.usecase
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

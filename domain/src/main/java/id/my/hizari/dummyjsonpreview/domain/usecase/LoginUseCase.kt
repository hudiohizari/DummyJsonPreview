package id.my.hizari.dummyjsonpreview.domain.usecase

import id.my.hizari.dummyjsonpreview.domain.model.User
import id.my.hizari.dummyjsonpreview.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * id.my.hizari.dummyjsonpreview.domain.usecase
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    // The password is passed through untouched, since leading or trailing spaces may be part of it.
    suspend operator fun invoke(username: String, password: String): User =
        repository.login(username = username.trim(), password = password)
}

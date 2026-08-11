package id.my.hizari.dummyjsonpreview.data.auth.api

import id.my.hizari.dummyjsonpreview.data.auth.model.UserDto
import retrofit2.http.GET

/**
 * id.my.hizari.dummyjsonpreview.data.auth.api
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

interface UserApi {

    @GET(value = "auth/me")
    suspend fun me(): UserDto
}

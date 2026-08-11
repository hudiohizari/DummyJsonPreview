package id.my.hizari.dummyjsonpreview.data.auth.mapper

import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshResponse
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthTokens

/**
 * id.my.hizari.dummyjsonpreview.data.auth.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun RefreshResponse.toDomain(): AuthTokens = AuthTokens(
    accessToken = accessToken.orEmpty(),
    refreshToken = refreshToken.orEmpty()
)

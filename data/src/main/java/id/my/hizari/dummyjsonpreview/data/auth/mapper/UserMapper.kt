package id.my.hizari.dummyjsonpreview.data.auth.mapper

import id.my.hizari.dummyjsonpreview.data.auth.model.UserDto
import id.my.hizari.dummyjsonpreview.domain.auth.model.User

/**
 * id.my.hizari.dummyjsonpreview.data.auth.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

fun UserDto.toDomain(): User = User(
    id = id ?: 0,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    gender = gender,
    image = image
)

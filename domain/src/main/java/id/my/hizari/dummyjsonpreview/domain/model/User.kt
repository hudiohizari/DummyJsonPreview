package id.my.hizari.dummyjsonpreview.domain.model

/**
 * id.my.hizari.dummyjsonpreview.domain.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

data class User(
    val id: Int,
    val username: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val gender: String?,
    val image: String?
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(separator = " ")
}

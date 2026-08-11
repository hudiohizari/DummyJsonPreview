package id.my.hizari.dummyjsonpreview.domain.error

/**
 * id.my.hizari.dummyjsonpreview.domain.error
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * The only error type that crosses the data to domain to presentation boundary, so the
 * presentation layer can branch on failures without depending on Retrofit or OkHttp.
 */
sealed class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {

    class Network(cause: Throwable? = null) : AppException("Network unavailable", cause)

    class Timeout(cause: Throwable? = null) : AppException("Connection timed out", cause)

    class Unauthorized(val serverMessage: String? = null) : AppException(serverMessage)

    class Http(val code: Int, val serverMessage: String? = null) : AppException(serverMessage)

    class Unknown(cause: Throwable? = null) : AppException(cause?.message, cause)
}

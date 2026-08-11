package id.my.hizari.dummyjsonpreview.data.network

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.data.network.ErrorResponse
import id.my.hizari.dummyjsonpreview.domain.error.AppException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/**
 * id.my.hizari.dummyjsonpreview.data.network
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * The single point where Retrofit and OkHttp failures become domain failures, so nothing above
 * the data layer has to know those libraries exist.
 */
@Singleton
class ApiErrorMapper @Inject constructor(
    private val gson: Gson
) {

    suspend fun <T> call(block: suspend () -> T): T =
        try {
            block()
        } catch (cancellation: CancellationException) {
            // Translating this would break structured concurrency.
            throw cancellation
        } catch (throwable: Throwable) {
            throw map(throwable = throwable)
        }

    fun map(throwable: Throwable): AppException = when (throwable) {
        is AppException -> throwable
        // These are all IOException subtypes, so the specific cases have to come first.
        is UnknownHostException, is ConnectException -> AppException.Network(cause = throwable)
        is SocketTimeoutException -> AppException.Timeout(cause = throwable)
        is HttpException -> throwable.toAppException()
        is IOException -> AppException.Network(cause = throwable)
        else -> AppException.Unknown(cause = throwable)
    }

    private fun HttpException.toAppException(): AppException {
        val serverMessage = runCatching(block = {
            gson.fromJson(response()?.errorBody()?.string(), ErrorResponse::class.java)?.message
        }).getOrNull()

        return if (code() == HTTP_UNAUTHORIZED) {
            AppException.Unauthorized(serverMessage = serverMessage)
        } else {
            AppException.Http(code = code(), serverMessage = serverMessage)
        }
    }

    companion object {
        const val HTTP_UNAUTHORIZED = 401
    }
}

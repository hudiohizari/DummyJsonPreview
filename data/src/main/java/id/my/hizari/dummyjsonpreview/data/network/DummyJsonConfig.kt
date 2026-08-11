package id.my.hizari.dummyjsonpreview.data.network

/**
 * id.my.hizari.dummyjsonpreview.data.network
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

object DummyJsonConfig {

    const val BASE_URL = "https://dummyjson.com/"

    /**
     * Access tokens default to 60 minutes, which would quietly end the session while the app is
     * still installed. A long window keeps the persistent session honest; expiry is still handled
     * by the refresh authenticator.
     */
    const val TOKEN_LIFETIME_MINUTES = 43200
}

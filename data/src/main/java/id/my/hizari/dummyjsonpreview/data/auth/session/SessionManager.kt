package id.my.hizari.dummyjsonpreview.data.auth.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.my.hizari.dummyjsonpreview.data.auth.api.TokenStore
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthSession
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthTokens
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.auth.session
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenStore {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey(name = "access_token")
        val REFRESH_TOKEN = stringPreferencesKey(name = "refresh_token")
        val USER_ID = intPreferencesKey(name = "user_id")
        val USERNAME = stringPreferencesKey(name = "username")
        val EMAIL = stringPreferencesKey(name = "email")
        val FIRST_NAME = stringPreferencesKey(name = "first_name")
        val LAST_NAME = stringPreferencesKey(name = "last_name")
        val GENDER = stringPreferencesKey(name = "gender")
        val IMAGE = stringPreferencesKey(name = "image")
    }

    // An unreadable store has to degrade to logged out rather than take the app down.
    private val preferences: Flow<Preferences> = dataStore.data
        .catch(action = { throwable ->
            if (throwable is IOException) emit(value = emptyPreferences()) else throw throwable
        })

    val currentUser: Flow<User?> = preferences.map(transform = { it.toUser() })

    suspend fun saveSession(session: AuthSession) {
        dataStore.edit(transform = { preferences ->
            preferences[Keys.ACCESS_TOKEN] = session.accessToken
            preferences[Keys.REFRESH_TOKEN] = session.refreshToken
            preferences.putUser(user = session.user)
        })
    }

    suspend fun saveUser(user: User) {
        dataStore.edit(transform = { preferences -> preferences.putUser(user = user) })
    }

    suspend fun hasActiveSession(): Boolean =
        preferences.first()[Keys.ACCESS_TOKEN].isNullOrBlank().not()

    suspend fun clearSession() {
        dataStore.edit(transform = { it.clear() })
    }

    override fun currentAccessToken(): String? = runBlocking(block = {
        preferences.first()[Keys.ACCESS_TOKEN]
    })

    override fun currentRefreshToken(): String? = runBlocking(block = {
        preferences.first()[Keys.REFRESH_TOKEN]
    })

    override fun saveTokens(tokens: AuthTokens) = runBlocking(block = {
        dataStore.edit(transform = { preferences ->
            preferences[Keys.ACCESS_TOKEN] = tokens.accessToken
            preferences[Keys.REFRESH_TOKEN] = tokens.refreshToken
        })
        Unit
    })

    override fun clear() = runBlocking(block = { clearSession() })

    private fun Preferences.toUser(): User? {
        val id = this[Keys.USER_ID]
        val token = this[Keys.ACCESS_TOKEN]
        if (id == null || token.isNullOrBlank()) return null

        return User(
            id = id,
            username = this[Keys.USERNAME],
            email = this[Keys.EMAIL],
            firstName = this[Keys.FIRST_NAME],
            lastName = this[Keys.LAST_NAME],
            gender = this[Keys.GENDER],
            image = this[Keys.IMAGE]
        )
    }

    private fun MutablePreferences.putUser(user: User) {
        this[Keys.USER_ID] = user.id
        user.username?.let(block = { this[Keys.USERNAME] = it })
        user.email?.let(block = { this[Keys.EMAIL] = it })
        user.firstName?.let(block = { this[Keys.FIRST_NAME] = it })
        user.lastName?.let(block = { this[Keys.LAST_NAME] = it })
        user.gender?.let(block = { this[Keys.GENDER] = it })
        user.image?.let(block = { this[Keys.IMAGE] = it })
    }
}

package id.my.hizari.dummyjsonpreview.data.auth.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthSession
import id.my.hizari.dummyjsonpreview.domain.auth.model.AuthTokens
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * id.my.hizari.dummyjsonpreview.data.auth.session
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class SessionManagerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var scope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        scope = CoroutineScope(context = Dispatchers.IO + SupervisorJob())
        dataStore = PreferenceDataStoreFactory.create(scope = scope) {
            File(temporaryFolder.root, "session.preferences_pb")
        }
        sessionManager = SessionManager(dataStore = dataStore)
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `there is no session before anything is stored`() = runTest(testBody = {
        assertFalse(sessionManager.hasActiveSession())
        assertNull(sessionManager.currentUser.first())
        assertNull(sessionManager.currentAccessToken())
    })

    @Test
    fun `a saved session round trips`() = runTest(testBody = {
        sessionManager.saveSession(session = session())

        assertTrue(sessionManager.hasActiveSession())
        assertEquals("access-token", sessionManager.currentAccessToken())
        assertEquals("refresh-token", sessionManager.currentRefreshToken())

        val user = sessionManager.currentUser.first()
        assertEquals(1, user!!.id)
        assertEquals("emilys", user.username)
        assertEquals("emily.johnson@x.dummyjson.com", user.email)
        assertEquals("Emily Johnson", user.fullName)
        assertEquals("https://dummyjson.com/icon/emilys/128", user.image)
    })

    @Test
    fun `clearing removes both the tokens and the cached profile`() = runTest(testBody = {
        sessionManager.saveSession(session = session())

        sessionManager.clear()

        assertFalse(sessionManager.hasActiveSession())
        assertNull(sessionManager.currentUser.first())
        assertNull(sessionManager.currentAccessToken())
        assertNull(sessionManager.currentRefreshToken())
    })

    @Test
    fun `refreshed tokens replace the old pair without disturbing the profile`() = runTest(testBody = {
        sessionManager.saveSession(session = session())

        sessionManager.saveTokens(
            tokens = AuthTokens(accessToken = "new-access", refreshToken = "new-refresh")
        )

        assertEquals("new-access", sessionManager.currentAccessToken())
        assertEquals("new-refresh", sessionManager.currentRefreshToken())
        assertEquals("Emily Johnson", sessionManager.currentUser.first()!!.fullName)
    })

    @Test
    fun `a refreshed profile replaces the cached one`() = runTest(testBody = {
        sessionManager.saveSession(session = session())

        sessionManager.saveUser(user = user(firstName = "Updated", lastName = "Name"))

        assertEquals("Updated Name", sessionManager.currentUser.first()!!.fullName)
        assertEquals("access-token", sessionManager.currentAccessToken())
    })

    /** The user is only meaningful while a token backs it, so a token-less store reads as null. */
    @Test
    fun `a profile without a token does not count as a session`() = runTest(testBody = {
        sessionManager.saveUser(user = user())

        assertFalse(sessionManager.hasActiveSession())
        assertNull(sessionManager.currentUser.first())
    })

    private fun user(firstName: String? = "Emily", lastName: String? = "Johnson") = User(
        id = 1,
        username = "emilys",
        email = "emily.johnson@x.dummyjson.com",
        firstName = firstName,
        lastName = lastName,
        gender = "female",
        image = "https://dummyjson.com/icon/emilys/128"
    )

    private fun session() = AuthSession(
        accessToken = "access-token",
        refreshToken = "refresh-token",
        user = user()
    )
}

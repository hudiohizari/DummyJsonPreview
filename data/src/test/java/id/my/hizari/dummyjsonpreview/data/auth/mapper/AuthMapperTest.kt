package id.my.hizari.dummyjsonpreview.data.auth.mapper

import com.google.gson.Gson
import id.my.hizari.dummyjsonpreview.data.auth.model.LoginResponse
import id.my.hizari.dummyjsonpreview.data.auth.model.RefreshResponse
import id.my.hizari.dummyjsonpreview.data.auth.model.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.data.auth.mapper
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class AuthMapperTest {

    private val gson = Gson()

    @Test
    fun `it splits the flat login response into tokens and a user`() {
        val response = gson.fromJson(LOGIN_JSON, LoginResponse::class.java)

        val session = response.toDomain()

        assertEquals("access-token-value", session.accessToken)
        assertEquals("refresh-token-value", session.refreshToken)
        assertEquals(1, session.user.id)
        assertEquals("emilys", session.user.username)
        assertEquals("emily.johnson@x.dummyjson.com", session.user.email)
        assertEquals("Emily Johnson", session.user.fullName)
        assertEquals("https://dummyjson.com/icon/emilys/128", session.user.image)
    }

    /**
     * A 200 without tokens is not something this API produces, but degrading to a blank token
     * keeps the session gate in control instead of crashing the login screen.
     */
    @Test
    fun `a login response missing its tokens degrades to blank rather than throwing`() {
        val response = gson.fromJson("""{"id":1,"username":"emilys"}""", LoginResponse::class.java)

        val session = response.toDomain()

        assertEquals("", session.accessToken)
        assertEquals("", session.refreshToken)
        assertEquals(1, session.user.id)
    }

    @Test
    fun `it maps a refresh response into a token pair`() {
        val response = gson.fromJson(
            """{"accessToken":"new-access","refreshToken":"new-refresh"}""",
            RefreshResponse::class.java
        )

        val tokens = response.toDomain()

        assertEquals("new-access", tokens.accessToken)
        assertEquals("new-refresh", tokens.refreshToken)
    }

    @Test
    fun `it maps the profile endpoint payload onto the user model`() {
        val dto = gson.fromJson(ME_JSON, UserDto::class.java)

        val user = dto.toDomain()

        assertEquals(1, user.id)
        assertEquals("emilys", user.username)
        assertEquals("Emily Johnson", user.fullName)
        assertEquals("female", user.gender)
    }

    @Test
    fun `a user payload with missing names still maps`() {
        val dto = gson.fromJson("""{"id":9}""", UserDto::class.java)

        val user = dto.toDomain()

        assertEquals(9, user.id)
        assertEquals("", user.fullName)
        assertNull(user.email)
    }

    private companion object {
        const val LOGIN_JSON = """
        {
          "id": 1,
          "username": "emilys",
          "email": "emily.johnson@x.dummyjson.com",
          "firstName": "Emily",
          "lastName": "Johnson",
          "gender": "female",
          "image": "https://dummyjson.com/icon/emilys/128",
          "accessToken": "access-token-value",
          "refreshToken": "refresh-token-value"
        }
        """

        const val ME_JSON = """
        {
          "id": 1,
          "username": "emilys",
          "email": "emily.johnson@x.dummyjson.com",
          "firstName": "Emily",
          "lastName": "Johnson",
          "gender": "female",
          "image": "https://dummyjson.com/icon/emilys/128",
          "address": { "city": "Phoenix" },
          "bank": { "cardNumber": "9289760655481815" }
        }
        """
    }
}

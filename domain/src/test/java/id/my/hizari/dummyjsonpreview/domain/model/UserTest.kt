package id.my.hizari.dummyjsonpreview.domain.model

import id.my.hizari.dummyjsonpreview.domain.stubUser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * id.my.hizari.dummyjsonpreview.domain.model
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

class UserTest {

    @Test
    fun `fullName joins the first and last name`() {
        val user = stubUser(firstName = "Emily", lastName = "Johnson")

        assertEquals("Emily Johnson", user.fullName)
    }

    @Test
    fun `fullName omits a missing last name without leaving a trailing space`() {
        val user = stubUser(firstName = "Emily", lastName = null)

        assertEquals("Emily", user.fullName)
    }

    @Test
    fun `fullName omits a missing first name without leaving a leading space`() {
        val user = stubUser(firstName = null, lastName = "Johnson")

        assertEquals("Johnson", user.fullName)
    }

    @Test
    fun `fullName is blank when both names are missing`() {
        val user = stubUser(firstName = null, lastName = null)

        assertEquals("", user.fullName)
    }
}

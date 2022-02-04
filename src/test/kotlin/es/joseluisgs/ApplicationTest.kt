package es.joseluisgs

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    private val testEnv = createTestEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
    }

    @Test
    fun trueIsTrue() {
        assertTrue(true)
    }

    @Test
    fun testRoot() = withApplication(testEnv) {
        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.contains("Hola Kotlin REST"))
        }
    }

    @Test
    fun testGetCustomers() = withApplication(testEnv) {
        with(handleRequest(HttpMethod.Get, "/rest//customers?limit=2")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.isNotEmpty())
            assertTrue(response.content!!.contains("chuck@norris.com"))

        }
    }
}
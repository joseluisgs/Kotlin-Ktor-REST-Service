package es.joseluisgs

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertTrue

/**
 * Recuerda arrancar el servidor antes!!!
 */
class EmbeddedServerTest {
    private val client = HttpClient()

    @Test
    fun testRoot() = runBlocking {
        val httpResponse: HttpStatement = client.get("http://localhost:6969/")
        val response: String = httpResponse.receive()
        assertTrue(response.contains("Hola Kotlin REST"))
    }

    @Test
    fun testGetCustomers() = runBlocking {
        val httpResponse: HttpStatement = client.get("http://localhost:6969/rest/customers?limit=2")
        val response: String = httpResponse.receive()
        assertTrue(response.isNotEmpty())
        assertTrue(response.contains("chuck@norris.com"))
    }
}
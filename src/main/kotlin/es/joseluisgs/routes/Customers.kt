package es.joseluisgs.routes

import es.joseluisgs.models.Customer
import es.joseluisgs.repositories.Customers
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

// Aplicamos las rutas a la aplicacion, extendiendo su routing
fun Application.customersRoutes() {
    routing {
        customersRoutes()
    }
}

// Definimos las rutas de este recurso
fun Route.customersRoutes() {
    route("rest/customers") {

        // GET /rest/customers/
        get {
            // Obtenemos el limite de registros a devolver
            var limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 0
            // Si no es vacio devuelve todos los customers
            limit = if (limit < 0) 0 else limit
            call.respond(Customers.getAll(limit))
        }

        // GET /rest/customers/{id}
        get("{id}") {
            // Si es nulo, hacemos un retur del error con @get para indicar que sale de esta parte del lambda,
            // si no saldríamos del metodo prinicipal y porque usamos call
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            // Buscamos el cliente con el id pasado, si no está devolvemos el error
            val customer =
                Customers.getById(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No customer with id $id")
                )

            call.respond(customer)
        }

        // PUT /rest/customers/{id}
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            try {
                val customer = call.receive<Customer>()
                if (Customers.update(id, customer)) {
                    call.respond(customer)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "No customer with id $id")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad JSON Data Body: ${e.message.toString()}")
                )
            }
        }

        // POST /rest/customers
        post {
            try {
                val customer = call.receive<Customer>()
                Customers.save(customer)
                call.respond(status = HttpStatusCode.Created, customer)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad JSON Data Body: ${e.message.toString()}")
                )
            }
        }

        // DELETE /rest/customers/{id}
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            if (Customers.delete(id)) {
                call.respond(HttpStatusCode.Accepted)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No customer with id $id")
                )
            }
        }

        get("{id}/orders") {
            // Si es nulo, hacemos un retur del error con @get para indicar que sale de esta parte del lambda,
            // si no saldríamos del metodo prinicipal y porque usamos call
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            // Buscamos el cliente con el id pasado, si no está devolvemos el error
            val orders =
                Customers.getOrders(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No orders for customer with id $id")
                )

            call.respond(orders)
        }
    }
}


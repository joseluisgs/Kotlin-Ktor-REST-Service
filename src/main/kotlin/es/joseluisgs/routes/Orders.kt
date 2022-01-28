package es.joseluisgs.routes

import es.joseluisgs.models.Order
import es.joseluisgs.repositories.Orders
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

// Aplicamos las rutas a la aplicacion, extendiendo su routing
fun Application.ordersRoutes() {
    routing {
        ordersRoutes()
    }
}

// Definimos las rutas de este recurso
fun Route.ordersRoutes() {
    route("rest/orders") {

        // GET /rest/orders/
        get {
            // Obtenemos el limite de registros a devolver
            var limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 0
            limit = if (limit < 0) 0 else limit
            call.respond(Orders.getAll(limit))
        }

        // GET /rest/orders/{id}
        get("{id}") {
            // Si es nulo, hacemos un retur del error con @get para indicar que sale de esta parte del lambda,
            // si no saldríamos del metodo prinicipal y porque usamos call
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            val order =
                Orders.getById(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No order with id $id")
                )

            call.respond(order)
        }

        // PUT /rest/orders/{id}
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            try {
                val order = call.receive<Order>()
                if (Orders.update(id, order)) {
                    call.respond(order)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "No order with id $id")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad JSON Data Body: ${e.message.toString()}")
                )
            }
        }

        // POST /rest/orders/
        post {
            try {
                val order = call.receive<Order>()
                Orders.save(order)
                call.respond(status = HttpStatusCode.Created, order)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad JSON Data Body: ${e.message.toString()}")
                )
            }
        }

        // DELETE /rest/orders/{id}
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            if (Orders.delete(id)) {
                call.respond(HttpStatusCode.Accepted)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No order with id $id")
                )
            }
        }

        // GET /rest/orders/{id}/contents
        get("{id}/contents") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            val contents =
                Orders.getContents(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No order with id $id")
                )

            call.respond(contents)
        }

        // GET /rest/orders/{id}/total
        get("{id}/total") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            val total =
                Orders.getTotal(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No order with id $id")
                )


            call.respond(mapOf("total" to total))
        }

        // GET /rest/orders/{id}/customer
        get("{id}/customer") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing or malformed id")
            )

            val customer =
                Orders.getCustomer(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "No order with id $id")
                )

            call.respond(customer)
        }
    }
}


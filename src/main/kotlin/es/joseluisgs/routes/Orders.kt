package es.joseluisgs.routes

import es.joseluisgs.error.ErrorResponse
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
            // Si no es vacio devuelve todos los customers
            if (!Orders.isEmpty()) {
                call.respond(Orders.getAll())
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "No orders found")
                )
            }
        }

        // GET /rest/orders/{id}
        get("{id}") {
            // Si es nulo, hacemos un retur del error con @get para indicar que sale de esta parte del lambda,
            // si no saldríamos del metodo prinicipal y porque usamos call
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Missing or malformed id")
            )

            // Buscamos el cliente con el id pasado, si no está devolvemos el error
            val order =
                Orders.getById(id) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "No order with id $id")
                )

            call.respond(order)
        }

        // PUT /rest/orders/{id}
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Missing or malformed id")
            )
            try {
                val order = call.receive<Order>()
                if (Orders.update(id, order)) {
                    call.respond(order)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse(HttpStatusCode.NotFound.value, "No order with id $id")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(HttpStatusCode.BadRequest.value, "Bad JSON Data Body: ${e.message.toString()}")
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
                    ErrorResponse(HttpStatusCode.BadRequest.value, "Bad JSON Data Body: ${e.message.toString()}")
                )
            }

        }

        // DELETE /rest/orders/{id}
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Missing or malformed id")
            )
            if (Orders.delete(id)) {
                call.respond(HttpStatusCode.Accepted)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "No order with id $id")
                )
            }
        }
    }
}


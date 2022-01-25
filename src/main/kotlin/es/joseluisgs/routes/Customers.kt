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

        // GET /rest/customer/
        get {
            // Si no es vacio devuelve todos los customers
            if (!Customers.isEmpty()) {
                call.respond(Customers.getAll())
            } else {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            }
        }

        // GET /rest/customers/{id}
        get("{id}") {
            // Si es nulo, encadenamos con un @ la llamada a un get para que devuleva el error
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            // Buscamos el cliente con el id pasado, si no está devolvemos el error
            val customer =
                Customers.getById(id) ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }

        // PUT /rest/customers/{id}
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            try {
                val customer = call.receive<Customer>()
                if (Customers.update(id, customer)) {
                    call.respond(customer)
                } else {
                    call.respondText("No customer with id $id", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Bad JSON Data Body: " + e.message.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        // POST /rest/customers
        post {
            try {
                val customer = call.receive<Customer>()
                Customers.save(customer)
                call.respond(status = HttpStatusCode.Created, customer)
            } catch (e: Exception) {
                call.respondText("Bad JSON Data Body: " + e.message.toString(), status = HttpStatusCode.BadRequest)
            }

        }

        // DELETE /rest/customers/{id}
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (Customers.delete(id)) {
                call.respond(HttpStatusCode.Accepted)
            } else {
                call.respondText("No customer with id $id", status = HttpStatusCode.NotFound)
            }
        }
    }
}


package es.joseluisgs.routes

import es.joseluisgs.repositories.Customers
import io.ktor.application.*
import io.ktor.http.*
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
            if (!Customers.isEmpty()) {
                call.respond(Customers.getAll())
            } else {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {

        }
        put("{id}") {

        }
        post {

        }
        delete("{id}") {

        }
    }
}


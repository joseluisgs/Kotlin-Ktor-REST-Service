package es.joseluisgs.routes

import es.joseluisgs.error.ErrorResponse
import es.joseluisgs.models.User
import es.joseluisgs.repositories.Users
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

// Extiendo Application
fun Application.authenticationRoutes() {
    routing {
        autheticationRoutes()
    }
}

// Definimos las rutas de este recurso
fun Route.autheticationRoutes() {
    route("rest/auth") {

        // POST /rest/auth/register
        post("/register") {
            lateinit var user: User
            try {
                user = call.receive()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(HttpStatusCode.BadRequest.value, "Bad JSON Data Body: ${e.message.toString()}")
                )
            }
            // Son buenos nuestros datos de registro
            if (Users.save(user)) {
                call.respond(HttpStatusCode.OK, user.toMap())
            } else {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        HttpStatusCode.BadRequest.value,
                        "Bad Credentials. Username exists or username >= 3 long && password >= 6 long"
                    )
                )
            }
        }

        post("/login") {
            lateinit var user: User
            try {
                user = call.receive()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(HttpStatusCode.BadRequest.value, "Bad JSON Data Body: ${e.message.toString()}")
                )
            }

            // Comprobamos las credenciales
            if (!Users.isValidCredentials(user.username, user.password)) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        HttpStatusCode.BadRequest.value,
                        "Bad Credentials.  username >= 3 long && password >= 6 long"
                    )
                )
            }

            // buscamos el usuario
            if (Users.checkUserNameAndPassword(user.username, user.password)) {
                // Si es correcto devolvemos el token
                call.respond(HttpStatusCode.OK, mapOf("token" to UUID.randomUUID().toString()))
            } else {
                return@post call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        HttpStatusCode.BadRequest.value,
                        "Invalid Username or Password"
                    )
                )
            }
        }

        // GET /rest/auth/users --> Solo si eres Admin, puedes ver todos los usuarios
        get("/users") {
            if (!Users.isEmpty()) {
                // Obtenemos el limite de registros a devolver
                val limit = call.request.queryParameters["limit"]?.toIntOrNull()
                call.respond(Users.getAll(limit))
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "No orders found")
                )
            }
        }
    }
}

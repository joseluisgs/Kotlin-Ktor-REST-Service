package es.joseluisgs.routes

import es.joseluisgs.models.Role
import es.joseluisgs.models.User
import es.joseluisgs.repositories.Users
import es.joseluisgs.services.TokenManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

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
                    mapOf("error" to "Bad JSON Data Body: ${e.message.toString()}")
                )
            }
            // Son buenos nuestros datos de registro
            if (Users.save(user)) {
                call.respond(HttpStatusCode.OK, user.toMap())
            } else {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad Credentials. Username exists or username >= 3 long && password >= 6 long")
                )
            }
        }

        post("/login") {
            var user: User?
            try {
                user = call.receive()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad JSON Data Body: ${e.message.toString()}")
                )
            }

            // Comprobamos las credenciales
            if (!Users.isValidCredentials(user!!.username, user!!.password)) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Bad Credentials.  username >= 3 long && password >= 6 long")
                )
            }

            // buscamos el usuario
            user = Users.checkUserNameAndPassword(user!!.username, user!!.password)
            // Si es correcto generamos el token y lo devolvemos, si es el usuario nulo, es error
            val token = user?.let { u -> TokenManager.generateJWTToken(u) } ?: return@post call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Invalid Username or Password")
            )
            call.respond(HttpStatusCode.OK, mapOf("token" to token))

        }

        // Estas rutas están autenticadas --> Protegidas por JWT
        authenticate {

            get("/me") {
                // Por el token me llega como principal (autenticado) el usuario en sus claims
                val principle = call.principal<JWTPrincipal>()
                val username = principle!!.payload.getClaim("username").asString()
                val userId = principle.payload.getClaim("userId").asString()
                val user = Users.findById(userId)
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "id" to user?.id,
                        "username" to user?.username,
                        "role" to user?.role.toString()
                    )
                )
            }

            // GET /rest/auth/users --> Solo si eres Admin, puedes ver todos los usuarios
            get("/users") {
                val principle = call.principal<JWTPrincipal>()
                val username = principle!!.payload.getClaim("username").asString()
                val userId = principle.payload.getClaim("userId").asString()
                val user = Users.findById(userId)
                if (user?.role == Role.ADMIN) {
                    if (!Users.isEmpty()) {
                        // Obtenemos el limite de registros a devolver
                        val limit = call.request.queryParameters["limit"]?.toIntOrNull()
                        call.respond(Users.getAll(limit))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "No orders found")
                        )
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "You are not authorized"))
                }
            }
        }
    }
}

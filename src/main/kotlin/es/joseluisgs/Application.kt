package es.joseluisgs

import es.joseluisgs.routes.*
import es.joseluisgs.services.TokenManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.cio.*


fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    // Si no le pasamos un puerto, leemos por defecto
    // val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    // Tambien podemos leer otras variables de la configuracion
    val presentacion = environment.config.propertyOrNull("mensajes.presentacion")?.getString() ?: "Hola Kotlin"
    val mode = environment.config.property("ktor.environment").getString()

    // Instalación de plugins y configuraciones

    // JWT token. Instalamos el plugin
    install(Authentication) {
        // Iniciamos con los datos el TokenManager
        TokenManager.init(
            environment.config.property("jwt.audience").getString(),
            environment.config.property("jwt.secret").getString(),
            environment.config.property("jwt.issuer").getString(),
            environment.config.property("jwt.expiration").getString().toLong()
        )
        // Configuramos el plugin
        jwt {
            // Cargamos el verificador con los datos de la configuracion
            verifier(TokenManager.verifyJWTToken())
            // con realm aseguramos la ruta que estamos protegiendo
            realm = environment.config.property("jwt.realm").getString()
            // Validamos el token con este middleware
            validate { jwtCredential ->
                // Si el token es valido, y tiene el campo del usuario para compararlo con el que nosotros
                // queremos devolvemos el JWTPrincipal
                if (jwtCredential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }
    }

    // Negocacion de contenidos en JSON
    install(ContentNegotiation) {
        json()
    }

    // Comenzamos a registrar las rutas
    routing {
        // Entrada en la api
        get("/") {
            call.respondText("$presentacion en modo $mode")
        }
    }

    // Registramos las rutas de la aplicación
    webRoutes()
    customersRoutes()
    ordersRoutes()
    uploadsRoutes()
    authenticationRoutes()
}
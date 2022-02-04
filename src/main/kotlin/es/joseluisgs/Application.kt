package es.joseluisgs

import es.joseluisgs.controller.DataBaseManager
import es.joseluisgs.controller.TokenManager
import es.joseluisgs.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import kotlinx.serialization.json.Json


fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    // Si no le pasamos un puerto, leemos por defecto
    // val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    // Tambien podemos leer otras variables de la configuracion
    val presentacion = environment.config.propertyOrNull("mensajes.presentacion")?.getString() ?: "Hola Kotlin"
    val mode = environment.config.property("ktor.environment").getString()

    // Instalación de plugins y configuraciones

    // Base de datos
    initDataBase()

    // JWT token. Instalamos el plugin
    initAuthentication()

    // Negocacion de contenidos en JSON
    initContentNegotiation()

    // Comenzamos a registrar las rutas
    initRoutes(presentacion, mode)
}


/**
 * Rutas principales del Servicio
 */
private fun Application.initRoutes(presentacion: String, mode: String) {
    routing {
        // Entrada en la api
        get("/") {
            call.respondText("$presentacion en modo $mode")
        }
    }

    // Registramos las rutas de la aplicación o controladores de rutas
    webRoutes()
    customersRoutes()
    ordersRoutes()
    uploadsRoutes()
    authenticationRoutes()
}

/**
 * Inicia el plugin de negocacion en JSON
 */
private fun Application.initContentNegotiation() {
    install(ContentNegotiation) {
        // Lo ponemos bonito :)
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}

/**
 * Instalamos el plugin de autenticación y configuramos JWT
 */
private fun Application.initAuthentication() {
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
}

/**
 * Iniciamos la configuración de Base de Datos
 */
private fun Application.initDataBase() {
    DataBaseManager.init(
        environment.config.property("database.jdbcUrl").getString(),
        environment.config.property("database.driverClassName").getString(),
        environment.config.property("database.username").getString(),
        environment.config.property("database.password").getString(),
        environment.config.property("database.maximumPoolSize").getString().toInt()
    )
}
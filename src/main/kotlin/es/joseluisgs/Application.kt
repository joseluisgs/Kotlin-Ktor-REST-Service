package es.joseluisgs

import es.joseluisgs.routes.customersRoutes
import es.joseluisgs.routes.webRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Si no le pasamos un puerto, leemos por defecto
    // val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    // Tambien podemos leer otras variables de la configuracion
    val presentacion = environment.config.propertyOrNull("mensajes.presentacion")?.getString() ?: "Hola Kotlin"
    val mode = environment.config.property("ktor.environment").getString()

    // Instalación de plugins y configuraciones
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

}
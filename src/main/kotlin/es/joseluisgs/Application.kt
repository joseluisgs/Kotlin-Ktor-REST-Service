package es.joseluisgs

import es.joseluisgs.routes.registerWebContentRouting
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    // Si no le pasamos un puerto, leemos por defecto
    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"
    // Tambien podemos leer otras variables de la configuracion
    val presentacion = environment.config.propertyOrNull("mensajes.presentacion")?.getString() ?: "Hola Kotlin"

    // Comenzamos a registrar las rutas
    routing {
        // Entrada en la api
        get("/") {
            call.respondText(presentacion)
        }
    }
    // Registramos las rutas de la aplicación
    registerWebContentRouting()
}
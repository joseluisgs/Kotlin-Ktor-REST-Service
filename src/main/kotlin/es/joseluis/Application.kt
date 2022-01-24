package es.joseluis

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    routing {
        // Entrada en la api
        get("/") {
            call.respondText(" \uD83D\uDC4B Hola Kotlin REST Service con Ktor")
        }
        // Contenido estático, desde la carpeta resoruces cuando entran a /web
        static("/web") {
            resources("web")
            default("index.html")
        }
    }
}
package es.joseluisgs.routes

import es.joseluisgs.error.ErrorResponse
import es.joseluisgs.services.Storage
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

var UPLOADS_DIR = "uploads"
var URL = ""

fun Application.uploadsRoutes() {
    // Para montar la URL completa del ficheros, necesito los datos del servidor
    initUploadsDirectory()

    routing {
        uploadsRoutes()
    }
}

private fun Application.initUploadsDirectory() {
    URL = environment.config.property("server.baseUrl").getString()
    UPLOADS_DIR = environment.config.property("uploads.dir").getString()

    // Si no existe el directorio, lo creamos
    if (!File(UPLOADS_DIR).exists()) {
        File(UPLOADS_DIR).mkdir()
    } else {
        // Si existe, borramos todos los ficheros // solo en dev
        if (environment.config.property("ktor.environment").getString() == "dev") {
            File(UPLOADS_DIR).listFiles()?.forEach { it.delete() }
        }
    }
}

fun Route.uploadsRoutes() {
    route("rest/uploads") {
        // POST /rest/uploads/
        post {
            // Recibimos el multiparte
            val multipartData = call.receiveMultipart()
            var fileDescription = ""
            var storage = emptyMap<String, String>()
            // Lo recorremos
            multipartData.forEachPart { part ->
                // Analizamos el tipo si es fichero
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        try {
                            storage = Storage.saveFile(UPLOADS_DIR, fileName, fileBytes)
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse(HttpStatusCode.InternalServerError.value, "ERROR: ${e.message}")
                            )
                        }
                    }
                }
                part.dispose()
            }
            call.respond(
                mapOf(
                    "originalName" to storage["originalName"],
                    "uploadName" to storage["uploadName"],
                    "url" to "$URL/rest/uploads/${storage["uploadName"]}"
                )
            )
        }

        // GET /rest/uploads/
        get("{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Missing or malformed file name")
            )
            try {
                val file = Storage.getFile(UPLOADS_DIR, fileName)
                // De esta manera lo podria visiualizar el navegador
                call.respondFile(file)
                // si lo hago así me pide descargar
                //call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "No file with name $fileName")
                )
            }
        }

        // DELETE /rest/uploads/
        delete("{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Missing or malformed file name")
            )
            try {
                Storage.deleteFile(UPLOADS_DIR, fileName)
                call.respond(HttpStatusCode.OK, "File $fileName deleted")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(HttpStatusCode.NotFound.value, "No file with name $fileName")
                )
            }
        }
    }
}
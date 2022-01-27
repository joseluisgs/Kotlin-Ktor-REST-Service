package es.joseluisgs.routes

import es.joseluisgs.error.ErrorResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File
import java.util.*

var UPLOADS_DIR = "uploads"
var URL = ""

fun Application.uploadsRoutes() {
    // Para montar la URL completa del ficheros, necesito los datos del servidor
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
    routing {
        uploadsRoutes()
    }
}

fun Route.uploadsRoutes() {
    route("rest/uploads") {
        // POST /rest/uploads/
        post {
            // Recibimos el multiparte
            val multipartData = call.receiveMultipart()
            var fileDescription = ""
            var fileName = ""
            var fileExtension = ""
            var fileUpload = ""
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        fileExtension = fileName.substringAfterLast(".")
                        fileUpload = UUID.randomUUID().toString() + "." + fileExtension
                        var fileBytes = part.streamProvider().readBytes()
                        File("$UPLOADS_DIR/$fileUpload").writeBytes(fileBytes)
                    }
                }
                part.dispose()
            }
            call.respond(
                mapOf(
                    "originalName" to fileName,
                    "uploadName" to fileUpload,
                    "url" to "$URL/rest/uploads/$fileUpload"
                )
            )
        }

        // GET /rest/uploads/
        get("{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(HttpStatusCode.BadRequest.value, "Missing or malformed file name")
            )
            val file = File("$UPLOADS_DIR/$fileName")
            if (file.exists()) {
                // De esta manera lo podria visiualizar el navegador
                call.respondFile(file)
                // si lo hago así me pide descargar
                //call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
            } else call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(HttpStatusCode.NotFound.value, "No file with name $fileName")
            )
        }
    }
}
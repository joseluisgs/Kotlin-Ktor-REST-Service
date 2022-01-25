package es.joseluisgs.routes

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*


// Ahora creamos una funcion de extensión de Aplication, y exportamos las rutas
fun Application.webRoutes() {
    routing {
        webRoutes()
    }
}

// Vamos a crear una ruta web, para ello usamos una función de extensión de la clase Router
// La llamamos webContent y le decimos el contenido que queremos que se muestre
fun Route.webRoutes() {
    // Contenido estático, desde la carpeta resources cuando entran a /web
    static {
        // Si nos preguntan por /web desde la raíz, le mandamos el contenido estático. tambien aplicamos redireccion
        resource("/web", "web/index.html")
        resource("*", "web/index.html")
        // todo contenido estático con web/, lo busca en la carpeta web
        static("web") {
            resources("web")
        }
    }
}


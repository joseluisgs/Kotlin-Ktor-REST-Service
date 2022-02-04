package es.joseluisgs.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    var id: String = "",
    var username: String,
    var password: String,
    val role: Role = Role.USER,
    val createdAt: String = LocalDateTime.now().toString(),
) {
    
    // Para quitar el Password
    fun toResponse(): Map<String, String> {
        return mapOf(
            "id" to id,
            "username" to username,
            "role" to role.toString(),
            "createdAt" to createdAt
        )
    }
}


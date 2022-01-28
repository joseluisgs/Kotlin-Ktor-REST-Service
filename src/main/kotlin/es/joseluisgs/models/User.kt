package es.joseluisgs.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class User(
    var id: String = UUID.randomUUID().toString(),
    var username: String,
    var password: String,
    val role: Role = Role.USER,
    val createdAt: String = LocalDateTime.now().toString(),
) {
    fun toMap(): Map<String, String> {
        return mapOf(
            "id" to id,
            "username" to username,
            "role" to role.toString(),
            "createdAt" to createdAt
        )
    }
}

enum class Role {
    USER, ADMIN
}
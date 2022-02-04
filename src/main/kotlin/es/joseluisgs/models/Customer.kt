package es.joseluisgs.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Customer(
    var id: String = "",
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: String = LocalDateTime.now().toString()
)
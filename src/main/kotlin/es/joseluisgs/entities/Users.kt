package es.joseluisgs.entities

import es.joseluisgs.models.Role
import es.joseluisgs.models.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

// Para hacer el DAO necesitamos la tabla y la clase que la mapea

// Tabla de usuarios
object UsersTable : LongIdTable() {
    val username = varchar("username", 100).index()
    val password = varchar("password", 150)
    val role = varchar("role", 50)
    val createdAt = datetime("created_at")
}

// Clase que mapea la tabla de usuarios
class Users(id: EntityID<Long>) : LongEntity(id) {
    // Sobre qué tabla me estoy trabajando
    companion object : LongEntityClass<Users>(UsersTable)

    var username by UsersTable.username
    var password by UsersTable.password
    var role by UsersTable.role
    var createdAt by UsersTable.createdAt

    fun toUser(): User {
        return User(
            id.toString(),
            username,
            password,
            Role.valueOf(role),
            createdAt.toString()
        )
    }

}
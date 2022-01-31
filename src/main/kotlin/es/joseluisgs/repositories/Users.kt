package es.joseluisgs.repositories

import es.joseluisgs.entities.Users
import es.joseluisgs.entities.UsersTable
import es.joseluisgs.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

object Users {

    fun findByUsername(username: String) = transaction {
        // addLogger(StdOutSqlLogger)
        Users.find { UsersTable.username eq username }.firstOrNull()?.toUser()
    }

    fun findById(id: String) = transaction {
        // addLogger(StdOutSqlLogger)
        Users.findById(id.toLong())?.toUser()
    }

    fun hashedPassword(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())

    fun isValidCredentials(username: String, password: String) = username.length >= 3 && password.length >= 6

    fun save(user: User): Boolean = transaction {
        //Credenciales correctas y no existe ese usario en el sistema
        // addLogger(StdOutSqlLogger)
        if (isValidCredentials(
                user.username,
                user.password
            ) && findByUsername(user.username) == null
        ) {
            user.username = user.username.lowercase()
            user.password = hashedPassword(user.password)

            user.id = Users.new {
                username = user.username
                password = user.password
                role = user.role.toString()
                createdAt = LocalDateTime.parse(user.createdAt)
            }.id.toString()
            true
        } else {
            false
        }
    }

    fun isEmpty() = transaction {
        // addLogger(StdOutSqlLogger)
        Users.all().empty()
    }

    fun getAll(limit: Int?): List<User> = transaction {
        // addLogger(StdOutSqlLogger)
        val response = if (limit == null) Users.all() else Users.all().limit(limit)
        return@transaction response.map { it.toUser() }
    }

    fun checkUserNameAndPassword(username: String, password: String): User? = transaction {
        // addLogger(StdOutSqlLogger)
        val user = findByUsername(username)
        return@transaction if (user != null && BCrypt.checkpw(password, user.password)) user
        else null
    }
}
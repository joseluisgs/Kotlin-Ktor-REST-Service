package es.joseluisgs.repositories

import es.joseluisgs.models.Role
import es.joseluisgs.models.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object Users {
    // Password 123456 en BCrypt
    val users = mutableListOf(
        User(
            "1", "admin",
            "\$2a\$10\$C3NO0nKdVaAdAi8cE18GA.ExGbYUPyNOrXKC.Clu/xtWdHCJiZ4hq",
            Role.ADMIN
        ),
        User(
            "2", "user",
            "\$2a\$10\$EB7BKG0b6OJp55YGwd0lVe/0Ys08y1LQmgrIhnweYTNHNF5PdzBn6",
            Role.USER
        )
    )

    fun findByUsername(username: String) = users.find { it.username == username }

    fun findById(id: String) = users.find { it.id == id }

    fun hashedPassword(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())

    fun isValidCredentials(username: String, password: String) = username.length >= 3 && password.length >= 6

    fun save(user: User): Boolean {
        //Credenciales correctas y no existe ese usario en el sistema
        return if (isValidCredentials(user.username, user.password) && findByUsername(user.username) == null) {
            user.username = user.username.lowercase()
            user.password = Users.hashedPassword(user.password)
            user.id = UUID.randomUUID().toString()
            users.add(user)
            true
        } else {
            false
        }
    }

    fun isEmpty() = users.isEmpty()

    fun getAll(limit: Int?): List<User> = if (limit == null) users else users.take(limit)

    fun checkUserNameAndPassword(username: String, password: String): Boolean {
        val user = findByUsername(username)
        return user != null && BCrypt.checkpw(password, user.password)
    }
}
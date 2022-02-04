package es.joseluisgs.entities

import es.joseluisgs.models.Customer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

// Tabla de usuarios
object CustomersTable : LongIdTable() {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 100)
    val createdAt = datetime("created_at")
}

// Clase que mapea la tabla de usuarios
class CustomerDAO(id: EntityID<Long>) : LongEntity(id) {
    // Sobre qué tabla me estoy trabajando
    companion object : LongEntityClass<CustomerDAO>(CustomersTable)

    var firstName by CustomersTable.firstName
    var lastName by CustomersTable.lastName
    var email by CustomersTable.email
    var createdAt by CustomersTable.createdAt

    // Mis orders relacion bidireccional
    val orders by OrderDAO referrersOn OrdersTable.customer


    fun toCustomer(): Customer {
        return Customer(
            id.toString(),
            firstName,
            lastName,
            email,
            createdAt.toString()
            // Mis orders
        )
    }
}
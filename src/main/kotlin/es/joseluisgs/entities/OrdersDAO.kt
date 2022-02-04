package es.joseluisgs.entities

import es.joseluisgs.models.Order
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

// Tabla de orders
object OrdersTable : LongIdTable() {
    val customer = reference("customer", CustomersTable)
    val createdAt = datetime("created_at")
}

// Clase que mapea la tabla de usuarios
class OrdersDAO(id: EntityID<Long>) : LongEntity(id) {
    // Sobre qué tabla me estoy trabajando
    companion object : LongEntityClass<OrdersDAO>(OrdersTable)

    var customer by CustomersDAO referencedOn OrdersTable.customer
    var createdAt by OrdersTable.createdAt

    // Relación inversa donde soy referenciado
    val contents by OrderItemsDAO referrersOn OrderItemsTable.order

    fun toOrder(): Order {
        return Order(
            id.toString(),
            customer.id.toString(),
            createdAt.toString(),
            contents.map { it.toOrderItem() }
        )
    }
}
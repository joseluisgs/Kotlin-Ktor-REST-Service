package es.joseluisgs.entities

import es.joseluisgs.models.Order
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

// Tabla de orders
object OrdersTable : LongIdTable() {
    val customer = reference("customer_id", CustomersTable)
    val createdAt = datetime("created_at")
}

// Clase que mapea la tabla de usuarios
class OrderDAO(id: EntityID<Long>) : LongEntity(id) {
    // Sobre qué tabla me estoy trabajando
    companion object : LongEntityClass<OrderDAO>(OrdersTable)

    var customer by CustomerDAO referencedOn OrdersTable.customer
    var createdAt by OrdersTable.createdAt

    // Relación inversa donde soy referenciado
    val contents by OrderItemDAO referrersOn OrderItemsTable.order

    fun toOrder(): Order {
        return Order(
            id.toString(),
            customer.id.toString(),
            createdAt.toString(),
            contents.map { it.toOrderItem() }
        )
    }
}
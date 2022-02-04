package es.joseluisgs.entities

import es.joseluisgs.models.OrderItem
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

// Tabla de orderItems
object OrderItemsTable : LongIdTable() {
    val item = varchar("item", 150)
    val amount = integer("amount")
    val price = double("price")
    val order = reference("order", OrdersTable)
}

// Clase que mapea la tabla
class OrderItemsDAO(id: EntityID<Long>) : LongEntity(id) {
    // Sobre qué tabla me estoy trabajando
    companion object : LongEntityClass<OrderItemsDAO>(OrderItemsTable)

    var item by OrderItemsTable.item
    var amount by OrderItemsTable.amount
    var price by OrderItemsTable.price
    var order by OrdersDAO referencedOn OrderItemsTable.order

    fun toOrderItem(): OrderItem {
        return OrderItem(
           item,
           amount,
           price,
           //order.toOrder()
        )
    }
}
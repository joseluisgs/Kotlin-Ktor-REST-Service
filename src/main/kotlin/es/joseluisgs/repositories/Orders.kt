package es.joseluisgs.repositories

import es.joseluisgs.models.Order
import es.joseluisgs.models.OrderItem
import java.util.*

object Orders : CrudRepository<Order, String> {
    val orders = mutableListOf(
        Order(
            "1",
            "1", listOf(
                OrderItem("Ham Sandwich", 2, 5.50),
                OrderItem("Water", 1, 1.50),
                OrderItem("Beer", 3, 2.30),
                OrderItem("Cheesecake", 1, 3.75)
            )
        ),
        Order(
            "2",
            "2", listOf(
                OrderItem("Cheeseburger", 1, 8.50),
                OrderItem("Water", 2, 1.50),
                OrderItem("Coke", 2, 1.76),
                OrderItem("Ice Cream", 1, 2.35)
            )
        ),
        Order(
            "3",
            "3", listOf(
                OrderItem("Water", 1, 1.50),
                OrderItem("Beer", 3, 2.30),
                OrderItem("Coke", 2, 1.76),
                OrderItem("Ice Cream", 1, 2.35)
            )
        ),
        Order(
            "4",
            "4", listOf(
                OrderItem("Ham Sandwich", 2, 5.50),
                OrderItem("Water", 1, 1.50),
                OrderItem("Coke", 2, 1.76),
                OrderItem("Cheeseburger", 1, 8.50),
            )
        )
    )

    fun isEmpty() = orders.isEmpty()

    override fun getAll() = orders.toList()

    override fun getById(id: String) = orders.find { it.id == id }

    override fun update(id: String, entity: Order): Boolean {
        val index = orders.indexOfFirst { it.id == id }
        return if (index >= 0) {
            // Por si nos ha llegado el id cambiado en el objeto distinto al de la ruta
            entity.id = id
            orders[index] = entity
            true
        } else {
            false
        }
    }

    override fun save(entity: Order) {
        entity.id = UUID.randomUUID().toString()
        orders.add(entity)
    }

    override fun delete(id: String) = orders.removeIf { it.id == id }
}
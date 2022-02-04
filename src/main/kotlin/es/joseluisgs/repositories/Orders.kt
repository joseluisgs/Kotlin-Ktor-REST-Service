package es.joseluisgs.repositories

import es.joseluisgs.entities.CustomersDAO
import es.joseluisgs.entities.OrderItemsDAO
import es.joseluisgs.entities.OrderItemsTable
import es.joseluisgs.entities.OrdersDAO
import es.joseluisgs.models.Order
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object Orders : CrudRepository<Order, String> {

    fun isEmpty() = transaction {
        OrdersDAO.all().empty()
    }

    override fun getAll(limit: Int): List<Order> = transaction {
        val response = if (limit == 0) OrdersDAO.all() else OrdersDAO.all().limit(limit)
        return@transaction response.map { it.toOrder() }
    }

    override fun getById(id: String): Order? = transaction {
        OrdersDAO.findById(id.toLong())?.toOrder()
    }

    override fun update(id: String, entity: Order) = transaction {
        var order = OrdersDAO.findById(id.toLong()) ?: return@transaction false
        val customer = CustomersDAO.findById(entity.customerID.toLong()) ?: return@transaction false

        order.apply {
            this.customer = customer
            createdAt = LocalDateTime.parse(entity.createdAt)
        }
        // Pro si en el bbody me han metido otra id que no corresponde con la que tenemos en la bd
        entity.id = id
        // Eliminamos los contents asociados para insertar los nuevos. Lo haré sin foreach con consulta
        OrderItemsTable.deleteWhere { OrderItemsTable.order eq order.id }
        // Insertamos los nuevos contents
        entity.contents.forEach {
            it.orderID = entity.id
            OrderItemsDAO.new {
                item = it.item
                amount = it.amount
                price = it.price
                this.order = OrdersDAO.findById(entity.id.toLong())!!
            }
        }
        return@transaction true
    }

    override fun save(entity: Order) = transaction {
        val customer = CustomersDAO.findById(entity.customerID.toLong()) ?: return@transaction
        entity.id = OrdersDAO.new {
            this.customer = customer
            createdAt = LocalDateTime.parse(entity.createdAt)
        }.id.toString()
        // Ahora debemos insertar los contenidos de la orden
        entity.contents.forEach {
            it.orderID = entity.id
            OrderItemsDAO.new {
                item = it.item
                amount = it.amount
                price = it.price
                order = OrdersDAO.findById(entity.id.toLong())!!
            }
        }
    }

    override fun delete(id: String) = transaction {
        // Borramos el order y su contenido
        val order = OrdersDAO.findById(id.toLong()) ?: return@transaction false
        // Con foreach el order y su conten
        // order.contents.forEach { it.delete() }
        // Con consulta
        OrderItemsTable.deleteWhere { OrderItemsTable.order eq order.id }
        order.delete()
        return@transaction true
    }

    fun getContents(id: String) = transaction {
        return@transaction OrdersDAO.findById(id.toLong())?.contents?.map { it.toOrderItem() }
    }

    fun getTotal(id: String) = transaction {
        return@transaction OrdersDAO.findById(id.toLong())?.contents?.sumOf { it.price * it.amount }
    }

    fun getCustomer(id: String) = transaction {
        return@transaction OrdersDAO.findById(id.toLong())?.customer?.toCustomer()
    }
}
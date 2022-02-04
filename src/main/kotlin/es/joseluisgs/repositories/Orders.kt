package es.joseluisgs.repositories

import es.joseluisgs.entities.CustomerDAO
import es.joseluisgs.entities.OrderDAO
import es.joseluisgs.entities.OrderItemDAO
import es.joseluisgs.entities.OrderItemsTable
import es.joseluisgs.models.Order
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object Orders : CrudRepository<Order, String> {

    fun isEmpty() = transaction {
        OrderDAO.all().empty()
    }

    override fun getAll(limit: Int): List<Order> = transaction {
        val response = if (limit == 0) OrderDAO.all() else OrderDAO.all().limit(limit)
        return@transaction response.map { it.toOrder() }
    }

    override fun getById(id: String): Order? = transaction {
        OrderDAO.findById(id.toLong())?.toOrder()
    }

    override fun update(id: String, entity: Order) = transaction {
        var order = OrderDAO.findById(id.toLong()) ?: return@transaction false
        val customer = CustomerDAO.findById(entity.customerId.toLong()) ?: return@transaction false

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
            it.orderId = entity.id
            OrderItemDAO.new {
                item = it.item
                amount = it.amount
                price = it.price
                this.order = OrderDAO.findById(entity.id.toLong())!!
            }
        }
        return@transaction true
    }

    override fun save(entity: Order) = transaction {
        val customer = CustomerDAO.findById(entity.customerId.toLong()) ?: return@transaction
        entity.id = OrderDAO.new {
            this.customer = customer
            createdAt = LocalDateTime.parse(entity.createdAt)
        }.id.toString()
        // Ahora debemos insertar los contenidos de la orden
        entity.contents.forEach {
            it.orderId = entity.id
            OrderItemDAO.new {
                item = it.item
                amount = it.amount
                price = it.price
                order = OrderDAO.findById(entity.id.toLong())!!
            }
        }
    }

    override fun delete(id: String) = transaction {
        // Borramos el order y su contenido
        val order = OrderDAO.findById(id.toLong()) ?: return@transaction false
        // Con foreach el order y su conten
        // order.contents.forEach { it.delete() }
        // Con consulta
        OrderItemsTable.deleteWhere { OrderItemsTable.order eq order.id }
        order.delete()
        return@transaction true
    }

    fun getContents(id: String) = transaction {
        return@transaction OrderDAO.findById(id.toLong())?.contents?.map { it.toOrderItem() }
    }

    fun getTotal(id: String) = transaction {
        return@transaction OrderDAO.findById(id.toLong())?.contents?.sumOf { it.price * it.amount }
    }

    fun getCustomer(id: String) = transaction {
        return@transaction OrderDAO.findById(id.toLong())?.customer?.toCustomer()
    }
}
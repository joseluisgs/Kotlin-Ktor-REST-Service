package es.joseluisgs.repositories

import es.joseluisgs.entities.CustomersDAO
import es.joseluisgs.entities.OrdersDAO
import es.joseluisgs.models.Order
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
        val order = OrdersDAO.findById(id.toLong()) ?: return@transaction false
        val customer = CustomersDAO.findById(entity.customerID.toLong()) ?: return@transaction false

        order.apply {
            this.customer = customer
            createdAt = LocalDateTime.parse(entity.createdAt)
        }
        return@transaction true
    }

    override fun save(entity: Order) = transaction {
        CustomersDAO.findById(entity.customerID.toLong()) ?: return@transaction
        entity.id = OrdersDAO.new {
            this.customer = customer
            createdAt = LocalDateTime.parse(entity.createdAt)
        }.id.toString()
    }

    override fun delete(id: String) = transaction {
        OrdersDAO.findById(id.toLong())?.delete().let { true }
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
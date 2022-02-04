package es.joseluisgs.repositories

import es.joseluisgs.entities.CustomerDAO
import es.joseluisgs.entities.OrderItemsTable
import es.joseluisgs.entities.OrdersTable
import es.joseluisgs.models.Customer
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio de clientes
 */
object Customers : CrudRepository<Customer, String> {

    fun isEmpty() = transaction {
        CustomerDAO.all().empty()
    }

    override fun getAll(limit: Int) = transaction {
        val response = if (limit == 0) CustomerDAO.all() else CustomerDAO.all().limit(limit)
        return@transaction response.map { it.toCustomer() }
    }

    override fun getById(id: String) = transaction {
        CustomerDAO.findById(id.toLong())?.toCustomer()
    }

    override fun update(id: String, entity: Customer) = transaction {
        val customer = CustomerDAO.findById(id.toLong()) ?: return@transaction false
        customer.apply {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            createdAt = LocalDateTime.parse(entity.createdAt)
        }
        // Pro si en el bbody me han metido otra id que no corresponde con la que tenemos en la bd
        entity.id = id
        return@transaction true
    }


    override fun save(entity: Customer) = transaction {
        entity.id = CustomerDAO.new {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            createdAt = LocalDateTime.parse(entity.createdAt)
        }.id.toString()
    }

    override fun delete(id: String) = transaction {
        // Primero debo eliminar las orders asociados y su contenido
        val customer = CustomerDAO.findById(id.toLong()) ?: return@transaction false
        // Podemos hacerlo con metodos de objetos o usando consultas
        /*customer.orders.forEach {
            it.contents.forEach { c ->
                c.delete()
            }
            it.delete()
        }*/
        // Con consultas de sql
        customer.orders.forEach {
            OrderItemsTable.deleteWhere { OrderItemsTable.order eq it.id }
            // it.delete() // Si tuvieras varios me ejecutaria esto varias veces, por eso en una consulta unica
        }
        OrdersTable.deleteWhere { OrdersTable.customer eq id.toLong() }
        customer.delete()
        return@transaction true
    }

    fun getOrders(id: String) = transaction {
        CustomerDAO.findById(id.toLong())?.orders?.map { it.toOrder() }
    }
}
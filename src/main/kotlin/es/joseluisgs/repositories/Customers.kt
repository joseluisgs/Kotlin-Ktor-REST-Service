package es.joseluisgs.repositories

import es.joseluisgs.entities.CustomersDAO
import es.joseluisgs.models.Customer
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio de clientes
 */
object Customers : CrudRepository<Customer, String> {

    fun isEmpty() = transaction {
        CustomersDAO.all().empty()
    }

    override fun getAll(limit: Int) = transaction {
        val response = if (limit == 0) CustomersDAO.all() else CustomersDAO.all().limit(limit)
        return@transaction response.map { it.toCustomer() }
    }

    override fun getById(id: String) = transaction {
        CustomersDAO.findById(id.toLong())?.toCustomer()
    }

    override fun update(id: String, entity: Customer) = transaction {
        val customer = CustomersDAO.findById(id.toLong()) ?: return@transaction false
        customer.apply {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            createdAt = LocalDateTime.parse(entity.createdAt)
        }

        return@transaction true
    }


    override fun save(entity: Customer) = transaction {
        entity.id = CustomersDAO.new {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            createdAt = LocalDateTime.parse(entity.createdAt)
        }.id.toString()
    }

    override fun delete(id: String) = transaction {
        CustomersDAO.findById(id.toLong())?.delete().let { true }
    }
}
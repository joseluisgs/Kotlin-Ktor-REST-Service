package es.joseluisgs.repositories

import es.joseluisgs.entities.Customers
import es.joseluisgs.models.Customer
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio de clientes
 */
object Customers : CrudRepository<Customer, String> {

    fun isEmpty() = transaction {
        Customers.all().empty()
    }

    override fun getAll(limit: Int): List<Customer> = transaction {
        val response = if (limit == 0) Customers.all() else Customers.all().limit(limit)
        return@transaction response.map { it.toCustomer() }
    }

    override fun getById(id: String): Customer? = transaction {
        Customers.findById(id.toLong())?.toCustomer()
    }

    override fun update(id: String, entity: Customer) = transaction {
        val customer = Customers.findById(id.toLong()) ?: return@transaction false
        customer.apply {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            createdAt = LocalDateTime.parse(entity.createdAt)
        }

        return@transaction true
    }


    override fun save(entity: Customer) = transaction {
        entity.id = Customers.new {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            createdAt = LocalDateTime.parse(entity.createdAt)
        }.id.toString()
    }

    override fun delete(id: String) = transaction {
        Customers.findById(id.toLong())?.delete().let { true }
    }
}
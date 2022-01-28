package es.joseluisgs.repositories

import es.joseluisgs.models.Customer
import java.util.*

/**
 * Repositorio de clientes
 */
object Customers : CrudRepository<Customer, String> {
    val customers = mutableListOf(
        Customer("1", "Chuck", "Norris", "chuck@norris.com"),
        Customer("2", "Bruce", "Wayne", "batman@iam.com"),
        Customer("3", "Peter", "Parker", "spiderman@iam.com"),
        Customer("4", "Tony", "Stark", "ironman@iam.com"),
        Customer("5", "Bruce", "Banner", "hulk@iam.com"),
        Customer("6", "Clark", "Kent", "superman@iam.com"),
        Customer("7", "Goku", "Son", "songoku@dragonball.com"),
        Customer("8", "Gohan", "Son", "songohan@dragonball.com"),
    )

    fun isEmpty() = customers.isEmpty()

    override fun getAll(limit: Int): List<Customer> = if (limit == 0) customers else customers.take(limit)

    override fun getById(id: String) = customers.find { it.id == id }

    override fun update(id: String, entity: Customer): Boolean {
        val index = customers.indexOfFirst { it.id == id }
        return if (index >= 0) {
            // Por si nos ha llegado el id cambiado en el objeto distinto al de la ruta
            entity.id = id
            customers[index] = entity
            true
        } else {
            false
        }
    }

    override fun save(entity: Customer) {
        entity.id = UUID.randomUUID().toString()
        customers.add(entity)
    }

    override fun delete(id: String) = customers.removeIf { it.id == id }

}
package es.joseluisgs.repositories

import es.joseluisgs.models.Customer
import java.util.*

object Customers {
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

    fun getAll() = customers.toList()

    fun getById(id: String) = customers.find { it.id == id }

    fun update(id: String, customer: Customer): Boolean {
        val index = customers.indexOfFirst { it.id == id }
        return if (index >= 0) {
            customer.id = id
            customers[index] = customer
            true
        } else {
            false
        }
    }

    fun save(customer: Customer) {
        customer.id = UUID.randomUUID().toString()
        customers.add(customer)
    }

    fun delete(id: String) = customers.removeIf { it.id == id }

}
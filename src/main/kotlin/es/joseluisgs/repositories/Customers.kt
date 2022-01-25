package es.joseluisgs.repositories

import es.joseluisgs.models.Customer

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


}
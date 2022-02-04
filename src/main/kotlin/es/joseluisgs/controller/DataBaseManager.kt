package es.joseluisgs.controller

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import es.joseluisgs.entities.*
import es.joseluisgs.models.*
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object DataBaseManager {
    private val logger = KotlinLogging.logger {}

    fun init(
        jdbcUrl: String,
        driverClassName: String,
        username: String,
        password: String,
        maximumPoolSize: Int = 10,
        initDatabaseData: Boolean = true
    ) {
        // Aplicamos Hiraki para la conexión a la base de datos
        println("Inicializando conexión a la base de datos")
        val config = HikariConfig()
        config.apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driverClassName
            this.username = username
            this.password = password
            this.maximumPoolSize = maximumPoolSize
        }
        Database.connect(HikariDataSource(config))
        logger.info { "Conexión a la base de datos inicializada" }

        // Otras funciones a realizar

        // Creamos las tablas
        createTables()

        // iniciamos los datos
        if (initDatabaseData) {
            initData()
        }
    }

    /**
     * Función para crear las tablas
     */
    private fun createTables() = transaction {
        addLogger(StdOutSqlLogger) // Para que se vea el log de consulas a la base de datos
        SchemaUtils.create(UsersTable, CustomersTable, OrdersTable, OrderItemsTable)
        logger.info { "Tablas creadas" }
    }

    /**
     * Función para inicializar los datos
     */
    private fun initData() {
        // Insertamos los datos.
        // Mira el directorio data
        initDataUsers()
        initDataCustomers()
        initDataOrders()
        logger.info { "Datos creados" }
    }

    private fun initDataOrders() {

        logger.info { "Orders de ejemplo insertados" }
    }

    private fun initDataCustomers() = transaction {
        val customers = listOf(
            Customer("1", "Chuck", "Norris", "chuck@norris.com"),
            Customer("2", "Bruce", "Wayne", "batman@iam.com"),
            Customer("3", "Peter", "Parker", "spiderman@iam.com"),
            Customer("4", "Tony", "Stark", "ironman@iam.com"),
            Customer("5", "Bruce", "Banner", "hulk@iam.com"),
            Customer("6", "Clark", "Kent", "superman@iam.com"),
            Customer("7", "Goku", "Son", "songoku@dragonball.com"),
            Customer("8", "Gohan", "Son", "songohan@dragonball.com"),
        )
        customers.forEach {
            CustomersDAO.new {
                this.firstName = it.firstName
                this.lastName = it.lastName
                this.email = it.email
                createdAt = LocalDateTime.now()
            }
        }
        logger.info { "Customers de ejemplo insertados" }
    }

    private fun initDataUsers() = transaction {
        val users = listOf(
            User(
                "1", "admin",
                "\$2a\$10\$C3NO0nKdVaAdAi8cE18GA.ExGbYUPyNOrXKC.Clu/xtWdHCJiZ4hq",
                Role.ADMIN
            ),
            User(
                "2", "user",
                "\$2a\$10\$EB7BKG0b6OJp55YGwd0lVe/0Ys08y1LQmgrIhnweYTNHNF5PdzBn6",
                Role.USER
            )
        )
        users.forEach {
            UsersDAO.new {
                username = it.username
                password = it.password
                role = it.role.toString()
                createdAt = LocalDateTime.now()
            }
        }
        logger.info { "Usuarios de ejemplo insertados" }
    }


}
package es.joseluisgs.controller

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DataBaseManager {
    fun init(jdbcUrl: String, driverClassName: String, username: String, password: String, maximumPoolSize: Int = 10) {
        // Aplicamos Hiraki para la conexión a la base de datos
        println("Inicializando conexión a la base de datos")
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.driverClassName = driverClassName
        config.username = username
        config.password = password
        config.maximumPoolSize = maximumPoolSize
        Database.connect(HikariDataSource(config))
        val dataSource = HikariDataSource(config)
        println("Database connected")

        // Otras funciones a realizar
        createTables()
    }

    private fun createTables() = transaction {
        addLogger(StdOutSqlLogger) // Para que se vea el log de consulas a la base de datos
        // SchemaUtils.create(UsersDSL, CitiesDSL, UsersTable, CitiesTable)
        println("Tables created")
    }
}
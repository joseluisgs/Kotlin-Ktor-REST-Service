package es.joseluisgs.repositories

interface CrudRepository<T, ID> {
    fun getAll(): List<T>
    fun getById(id: ID): T?
    fun update(id: ID, entity: T): Boolean
    fun save(entity: T)
    fun delete(id: ID): Boolean
}
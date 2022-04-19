import java.sql.Connection
import java.sql.SQLException

/*Clase que establece la estructura de un DAO, con algunos métodos ya establecidos
* Hay que introducirle el tipo de la tabla de la que se quiere hacer el DAO y el tipo del ID que usa. */
abstract class DAO<T, idType>(protected val c: Connection) {

    protected abstract val SCHEMA: String
    protected abstract val TABLE: String
    protected abstract val TRUNCATE_TABLE_SQL: String
    protected abstract val CREATE_TABLE_SQL: String
    protected abstract val INSERT_SQL: String
    protected abstract val SELECT_BY_ID: String
    protected abstract val SELECT_ALL: String
    protected abstract val DELETE_SQL: String
    protected abstract val UPDATE_SQL: String

    open fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (rs.next()) truncateTable() else createTable()
    }

    fun truncateTable() {
        println(TRUNCATE_TABLE_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun createTable() {
        println(CREATE_TABLE_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun printSQLException(ex: SQLException) {
        for (e in ex) {
            if (e is SQLException) {
                e.printStackTrace(System.err)
                System.err.println("SQLState: " + e.sqlState)
                System.err.println("Error Code: " + e.errorCode)
                System.err.println("Message: " + e.message)
                var t = ex.cause
                while (t != null) {
                    println("Cause: $t")
                    t = t.cause
                }
            }
        }
    }

    abstract fun insert(objeto: T)
    abstract fun selectAll(): List<T>
    abstract fun selectById(id: idType): T?
    abstract fun update(objeto: T): Boolean
    abstract fun deleteById(id: idType): Boolean
}
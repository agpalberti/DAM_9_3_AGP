import java.sql.Connection
import java.sql.SQLException

class InventariosDAO(private val c: Connection) {

    // En el companion object creamos todas las constantes.
    // Las constante definirán las plantillas de las sentencias que necesitamos para construir
    // los selects, inserts, deletes, updates.

    // En aquellos casos en donde necesitemos insertar un parametro, pondremos un ?
    // Luego lo sistituiremos llamando a métodos setX, donde X será (Int, String, ...)
    // dependiendo del tiempo de dato que corresponda.
    companion object {
        private const val SCHEMA = "default"
        private const val TABLE = "INVENTARIOS"
        private const val TRUNCATE_TABLE_INVENTARIOS_SQL = "TRUNCATE TABLE INVENTARIOS"
        private const val CREATE_TABLE_INVENTARIOS_SQL =
            "CREATE TABLE INVENTARIOS (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200) )"
        private const val INSERT_INVENTARIOS_SQL = "INSERT INTO INVENTARIOS" + "  (ID_TIENDA, NOMBRE_TIENDA, DIRECCION_TIENDA) VALUES " + " (?, ?, ?)"
        private const val SELECT_INVENTARIOS_BY_ID = "select ID_TIENDA, NOMBRE_TIENDA, DIRECCION_TIENDA from INVENTARIOS where ID_TIENDA =?"
        private const val SELECT_ALL_INVENTARIOS = "select * from INVENTARIOS"
        private const val DELETE_INVENTARIOS_SQL = "delete from INVENTARIOS where ID_TIENDA = ?"
        private const val UPDATE_INVENTARIOS_SQL = "update INVENTARIOS set ID_TIENDA = ?, NOMBRE_TIENDA = ?, DIRECCION_TIENDA = ? where ID_TIENDA = ?"
    }


    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (!rs.next())  truncateTable() else createTable()
    }

    private fun truncateTable() {
        println(TRUNCATE_TABLE_INVENTARIOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_INVENTARIOS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun createTable() {
        println(CREATE_TABLE_INVENTARIOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_INVENTARIOS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    /**
     * Insert Inserta un objeto MyUser en la base de datos.
     * El proceso siempre es el mismo:
     *      Haciendo uso de la conexión, prepara una Statement pasandole la sentencia que se va a ejecutar
     *      en este caso, INSERT_USERS_SQL
     *      A la Statement devuelta se le aplica use
     *          Establecemos los valores por cada ? que existan en la plantilla.
     *          En este caso son 3, pq en INSERT_USERS_SQL hay tres ?
     *          Los indices tienen que ir en el mismo orden en el que aparecen
     *
     *          Finalmente, se ejecuta la Statement
     *          Se llama a commit.
     *
     * @param tienda
     */
    fun insert(tienda: Tienda) {
        println(INSERT_INVENTARIOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_INVENTARIOS_SQL).use { st ->
                st.setInt(1, tienda.id)
                st.setString(2, tienda.nombre)
                st.setString(3, tienda.direccion)
                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: String): Tienda? {
        var tienda: Tienda? = null
        // Step 1: Preparamos la Statement, asignado los valores a los indices
        //          en función de las ? que existen en la plantilla
        try {
            c.prepareStatement(SELECT_INVENTARIOS_BY_ID).use { st ->
                st.setString(1, id)
                println(st)
                // Step 3: Ejecuta la Statement
                val rs = st.executeQuery()

                // Step 4: Procesamos el objeto ResultSet (rs), mientras tenga valores.
                //          En este caso, si hay valores, tendrá un unico valor, puesto
                //          que estamos buscando por el ID, que es la clave primaria.
                while (rs.next()) {
                    val id = rs.getInt("author")
                    val nombre = rs.getString("title")
                    val direccion = rs.getString("genre")
                    tienda = Tienda(id,nombre,direccion)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tienda
    }

    fun selectAll(): List<Tienda> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val INVENTARIOS: MutableList<Tienda> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL_INVENTARIOS).use { st ->
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("author")
                    val nombre = rs.getString("title")
                    val direccion = rs.getString("genre")
                    INVENTARIOS.add(Tienda(id,nombre,direccion))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return INVENTARIOS
    }

    fun deleteById(id: String): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_INVENTARIOS_SQL).use { st ->
                st.setString(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    fun update(tienda: Tienda): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_INVENTARIOS_SQL).use { st ->
                st.setInt(1, tienda.id)
                st.setString(2, tienda.nombre)
                st.setString(3, tienda.direccion)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

    private fun printSQLException(ex: SQLException) {
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


}
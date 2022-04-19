import java.sql.Connection
import java.sql.SQLException

/*Clase con las herramientas para utilizar la tabla tiendas. Hereda de DAO*/
class TiendasDAO(c: Connection) : DAO<Tienda, Long>(c) {

    override val SCHEMA = "default"
    override val TABLE = "TIENDAS"
    override val TRUNCATE_TABLE_SQL = "TRUNCATE TABLE TIENDAS"
    override val CREATE_TABLE_SQL =
        "CREATE TABLE TIENDAS (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200) )"
    override val INSERT_SQL =
        "INSERT INTO TIENDAS" + "  (ID_TIENDA, NOMBRE_TIENDA, DIRECCION_TIENDA) VALUES " + " (?, ?, ?)"
    override val SELECT_BY_ID = "select ID_TIENDA, NOMBRE_TIENDA, DIRECCION_TIENDA from TIENDAS where ID_TIENDA =?"
    override val SELECT_ALL = "select * from TIENDAS"
    override val DELETE_SQL = "delete from TIENDAS where ID_TIENDA = ?"
    override val UPDATE_SQL = "update TIENDAS set NOMBRE_TIENDA = ?, DIRECCION_TIENDA = ? where ID_TIENDA = ?"

    override fun insert(objeto: Tienda) {
        println(INSERT_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_SQL).use { st ->
                st.setLong(1, objeto.id)
                st.setString(2, objeto.nombre)
                st.setString(3, objeto.direccion)
                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    override fun selectById(id: Long): Tienda? {
        var tienda: Tienda? = null
        // Step 1: Preparamos la Statement, asignado los valores a los indices
        //          en función de las ? que existen en la plantilla
        try {
            c.prepareStatement(SELECT_BY_ID).use { st ->
                st.setLong(1, id)
                println(st)
                // Step 3: Ejecuta la Statement
                val rs = st.executeQuery()

                // Step 4: Procesamos el objeto ResultSet (rs), mientras tenga valores.
                //          En este caso, si hay valores, tendrá un unico valor, puesto
                //          que estamos buscando por el ID, que es la clave primaria.
                while (rs.next()) {
                    val nombre = rs.getString("NOMBRE_TIENDA")
                    val direccion = rs.getString("DIRECCION_TIENDA")
                    tienda = Tienda(id, nombre, direccion)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tienda
    }

    override fun selectAll(): List<Tienda> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val tiendas: MutableList<Tienda> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL).use { st ->
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getLong("ID_TIENDA")
                    val nombre = rs.getString("NOMBRE_TIENDA")
                    val direccion = rs.getString("DIRECCION_TIENDA")
                    tiendas.add(Tienda(id, nombre, direccion))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tiendas
    }

    override fun deleteById(id: Long): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_SQL).use { st ->
                st.setLong(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    override fun update(objeto: Tienda): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_SQL).use { st ->
                st.setLong(3, objeto.id)
                st.setString(1, objeto.nombre)
                st.setString(2, objeto.direccion)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

}
import java.sql.Connection
import java.sql.SQLException

//Clase que permite utilizar la tabla Inventarios.
class InventariosDAO(c: Connection) : DAO<Inventario, Long>(c) {

    override val SCHEMA = "default"
    override val TABLE = "INVENTARIOS"
    override val TRUNCATE_TABLE_SQL = "TRUNCATE TABLE INVENTARIOS"
    override val CREATE_TABLE_SQL =
        "CREATE TABLE INVENTARIOS (ID_ARTICULO NUMBER(10,0) CONSTRAINT PK_ID_ARTICULO PRIMARY KEY, NOMBRE VARCHAR2(50) UNIQUE, COMENTARIO VARCHAR2(200) NOT NULL, PRECIO NUMBER(10,2) CHECK(PRECIO>0), ID_TIENDA NUMBER(10,0) CONSTRAINT FK_ID_TIENDA REFERENCES TIENDAS(ID_TIENDA) )"
    override val INSERT_SQL =
        "INSERT INTO INVENTARIOS" + "  (ID_ARTICULO, NOMBRE, COMENTARIO, PRECIO, ID_TIENDA) VALUES " + " (?, ?, ?, ?, ?)"
    override val SELECT_BY_ID =
        "select ID_ARTICULO, NOMBRE, COMENTARIO, PRECIO, ID_TIENDA from INVENTARIOS where ID_ARTICULO =?"
    override val SELECT_ALL = "select * from INVENTARIOS"
    override val DELETE_SQL = "delete from INVENTARIOS where ID_ARTICULO = ?"
    override val UPDATE_SQL =
        "update INVENTARIOS set NOMBRE = ?, COMENTARIO = ?, PRECIO = ?, ID_TIENDA = ? where ID_ARTICULO = ?"
    val CREATE_INDEX_ON_TABLE = "CREATE INDEX IDX_ID_TIENDA ON INVENTARIOS(ID_TIENDA)"
    val SELECT_BY_TIENDA = "select * from INVENTARIOS where ID_TIENDA = ?"

    //Modificada para incorporar el INDEX
    override fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (rs.next()) truncateTable() else {
            createTable()
            c.prepareStatement(CREATE_INDEX_ON_TABLE).executeUpdate()
        }
    }

    override fun insert(objeto: Inventario) {
        println(INSERT_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_SQL).use { st ->
                st.setLong(1, objeto.id_articulo)
                st.setString(2, objeto.nombre)
                st.setString(3, objeto.comentario)
                st.setLong(5, objeto.id_tienda)
                //Modificado para implementar el aumento del 15% en el precio en caso de que el precio supere los 2000
                if (objeto.precio < 2000.00) {
                    st.setDouble(4, objeto.precio)
                } else {
                    st.setDouble(4, objeto.precio * 1.15)
                }
                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    override fun selectById(id: Long): Inventario? {
        var inventario: Inventario? = null
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
                    val id = rs.getLong("id_articulo")
                    val nombre = rs.getString("nombre")
                    val comentario = rs.getString("comentario")
                    val precio = rs.getDouble("precio")
                    val id_tienda = rs.getLong("id_tienda")
                    inventario = Inventario(id, nombre, comentario, precio, id_tienda)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventario
    }

    override fun selectAll(): List<Inventario> {
        val inventarios: MutableList<Inventario> = ArrayList()
        try {
            c.prepareStatement(SELECT_ALL).use { st ->
                println(st)
                val rs = st.executeQuery()
                while (rs.next()) {
                    val id = rs.getLong("id_articulo")
                    val nombre = rs.getString("nombre")
                    val comentario = rs.getString("comentario")
                    val precio = rs.getDouble("precio")
                    val id_tienda = rs.getLong("id_tienda")
                    inventarios.add(Inventario(id, nombre, comentario, precio, id_tienda))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventarios
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

    override fun update(objeto: Inventario): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_SQL).use { st ->
                st.setLong(5, objeto.id_articulo)
                st.setString(1, objeto.nombre)
                st.setString(2, objeto.comentario)
                st.setDouble(3, objeto.precio)
                st.setLong(4, objeto.id_tienda)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

    //Devuelve una lista del inventario según la tienda especificada
    fun selectInventarioByTienda(objeto: Tienda): List<Inventario> {
        val inventarios: MutableList<Inventario> = ArrayList()
        try {
            c.prepareStatement(SELECT_BY_TIENDA).use { st ->
                st.setLong(1, objeto.id)
                println(st)
                val rs = st.executeQuery()
                while (rs.next()) {
                    val id = rs.getLong("id_articulo")
                    val nombre = rs.getString("nombre")
                    val comentario = rs.getString("comentario")
                    val precio = rs.getDouble("precio")
                    val id_tienda = rs.getLong("id_tienda")
                    inventarios.add(Inventario(id, nombre, comentario, precio, id_tienda))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventarios
    }

}
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.SQLException

fun main() {
    val c = ConnectionBuilder()
    println("conectando.....")

    if (c.connection.isValid(10)) {
        println("Conexión válida")

        // Deshabilito el autoCommit. Si no, tengo que quitar los commit()
        c.connection.autoCommit = false

        // Uso la conexión. De esta manera cierra la conexión cuando termine el bloque
        c.connection.use {

            // Me creo mi objeto DAO (Data Access Object), el cual sabe acceder a los ç
            // datos de la tabla USER. Necesita la conexión (it) para poder acceder a la
            // base de datos.
            // El objeto DAO va a tener todos los metodos necesarios para trabajar con
            // la tabla USER, y devolverá entidades MyUser.
            // Fuera de este objeto no debería hablarse de nada relacioando con la base
            // de datos.
            // Los objetos MyUser, tambien llamados entidades, se llaman
            // Objetos TO (Transfer Object) porque son objetos que transfieren datos.
            // desde la base de datos hasta las capas de logica de negocio y presentación.
            val librosDAO = TiendasDAO(it)

            // Creamos la tabla o la vaciamos si ya existe
            librosDAO.prepareTable()
            librosDAO.insert(Tienda(id = "bk123", author = "El Parra", title = "La vida de Ricardo", genre = "Demiurgo", price = 20.00, publish_date = Date(2000,6,21), description = "Muchas gracias por leer mi libro" )) // Buscar un usuario
            val b = librosDAO.selectById("1")

            // Si ha conseguido el usuario, por tanto no es nulo, entonces
            // actualizar el usuario
            if (b!=null)
            {
                b.author = "Ricardo Gallego"
                librosDAO.update(b)
            }
            // Borrar un usuario
            librosDAO.deleteById("2")

            // Seleccionar todos los usuarios
            println(librosDAO.selectAll())
        }
    } else
        println("Conexión ERROR")
}

/**
 * Connection builder construye una conexión
 *
 * @constructor Create empty Connection builder
 */
class ConnectionBuilder {
    // TODO Auto-generated catch block
    lateinit var connection: Connection

    // La URL de conexión. Tendremos que cambiarsa según el SGBD que se use.
    private val jdbcURL = "jdbc:oracle:thin:@localhost:1521:XE"
    private val jdbcUsername = "programacion"
    private val jdbcPassword = "programacion"

    init {
        try {
            // Aqui construimos la conexión
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)
        } catch (e: SQLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
    // Si termina sin excepción, habrá creado la conexión.

}
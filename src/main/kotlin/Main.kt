fun main() {
    val c = ConnectionBuilder()
    println("conectando.....")

    if (c.connection.isValid(10)) {
        println("Conexión válida")

        // Deshabilito el autoCommit. Si no, tengo que quitar los commit()
        c.connection.autoCommit = false

        // Uso la conexión. De esta manera cierra la conexión cuando termine el bloque
        c.connection.use {

            val tiendasDAO = TiendasDAO(it)
            val inventariosDAO = InventariosDAO(it)

            tiendasDAO.prepareTable()
            tiendasDAO.insert(Tienda(1,"Bricolage Alberti","Camino El Cogollo 12"))
            val t = tiendasDAO.selectById(1)

            inventariosDAO.prepareTable()
            inventariosDAO.insert(Inventario(101,"Tornillo cuadrado","Pagado", 1.50,1))
            val i = inventariosDAO.selectById(1)

            if (t!=null)
            {
                t.nombre = "Ferreteria Alberti"
                tiendasDAO.update(t)
            }

            tiendasDAO.deleteById(1)
            println(tiendasDAO.selectAll())

            if (i!=null){
                i.nombre = "Tornillo redondo"
                inventariosDAO.update(i)
            }
            inventariosDAO.deleteById(10)
            println(inventariosDAO.selectAll())
        }

    } else
        println("Conexión ERROR")
}
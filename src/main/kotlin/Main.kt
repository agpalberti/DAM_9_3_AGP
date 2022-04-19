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
            with(tiendasDAO) {
                insert(Tienda(1, "La Nena", "Callejon de la Nena #123, Colonia Dulce Amor"))
                insert(Tienda(2, "La Virgen", "Calle Rosa de Guadalupe #2, Colonia Bajo del Cerro"))
                insert(Tienda(3, "La Piscina", "Avenida de los Charcos #78, Colonia El Mojado"))
                insert(Tienda(4, "El Churro", "Calle el Pason #666, Colonia El Viaje"))
                insert(Tienda(5, "Don Pancho", "Avenida del Reboso #1521, Colonia El Burro"))
            }
            val t = tiendasDAO.selectById(1)

            inventariosDAO.prepareTable()
            with(inventariosDAO) {
                insert(Inventario(1, "CD-DVD", "900 MB DE ESPACIO", 35.50, 5))
                insert(Inventario(2, "USB-HP", "32GB, USB 3.0", 155.90, 4))
                insert(Inventario(3, "Laptop SONY", "4GB RAM, 300 HDD, i5 2.6 GHz.", 13410.07, 3))
                insert(Inventario(4, "Mouse Optico", "700 DPI", 104.40, 2))
                insert(Inventario(5, "Disco Duro", "200 TB, HDD, USB 3.0", 2300.00, 1))
                insert(Inventario(6, "Proyector TSHB", "TOSHIBA G155", 5500.00, 5))
            }

            val listatiendas = tiendasDAO.selectAll()
            val listaInventarioPorTienda1 = tiendasDAO.selectById(1)
                ?.let { it1 -> inventariosDAO.selectInventarioByTienda(it1) }
            println("Todas las tiendas: $listatiendas")
            println("Inventario de la tienda 1: $listaInventarioPorTienda1")
        }

    } else
        println("Conexión ERROR")
}

/*Clase que guarda información de un inventario. El id debe ser Long para que quepa*/
data class Inventario(
    val id_articulo: Long,
    var nombre: String,
    val comentario: String,
    val precio: Double,
    val id_tienda: Long,
) {

}

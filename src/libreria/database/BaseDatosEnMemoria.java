package libreria.database;

import libreria.model.Autor;
import libreria.model.Categoria;
import libreria.model.Cliente;
import libreria.model.Libro;
import libreria.model.Proveedor;
import libreria.model.Venta;

public class BaseDatosEnMemoria {
    public final Almacen<Autor> autores = new Almacen<>();
    public final Almacen<Categoria> categorias = new Almacen<>();
    public final Almacen<Proveedor> proveedores = new Almacen<>();
    public final Almacen<Cliente> clientes = new Almacen<>();
    public final Almacen<Libro> libros = new Almacen<>();
    public final Almacen<Venta> ventas = new Almacen<>();
    public final Almacen<Object> secuenciaDetalles = new Almacen<>();
}

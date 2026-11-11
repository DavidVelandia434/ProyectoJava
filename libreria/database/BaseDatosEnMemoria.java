/*
 * Programa : Sistema de gestión de librería
 * Archivo  : BaseDatosEnMemoria.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.database;

import libreria.model.Autor;
import libreria.model.Categoria;
import libreria.model.Cliente;
import libreria.model.Libro;
import libreria.model.Proveedor;
import libreria.model.Venta;

/**
 * ============================================================
 *  ESPACIO PARA EMULAR LA BASE DE DATOS
 * ============================================================
 * Mientras no exista la base de datos real, cada "tabla" se emula con un Almacen.
 * Los DAO de libreria.dao.memoria trabajan sobre esta clase.
 *
 * Cuando tengas MySQL/PostgreSQL, esta clase deja de usarse: se crean las
 * implementaciones de libreria.dao.jdbc y se cambian en Main.
 * El resto del programa (service, ui) NO cambia.
 */
public class BaseDatosEnMemoria {
    public final Almacen<Autor> autores = new Almacen<>();
    public final Almacen<Categoria> categorias = new Almacen<>();
    public final Almacen<Proveedor> proveedores = new Almacen<>();
    public final Almacen<Cliente> clientes = new Almacen<>();
    public final Almacen<Libro> libros = new Almacen<>();
    public final Almacen<Venta> ventas = new Almacen<>();
    /** Secuencia para los ids de detalle_venta (los detalles viven dentro de Venta). */
    public final Almacen<Object> secuenciaDetalles = new Almacen<>();
}

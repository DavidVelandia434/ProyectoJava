/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Categoria.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.model;

/** Categoría o género de los libros. */
public class Categoria {
    private Integer id;
    private String nombre;

    public Categoria(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre;
    }
}

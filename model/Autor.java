/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Autor.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.model;

/** Autor de uno o varios libros (relación muchos a muchos con Libro). */
public class Autor {
    private Integer id;
    private String nombre;
    private String nacionalidad;

    public Autor(Integer id, String nombre, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " (" + nacionalidad + ")";
    }
}

/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Proveedor.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.model;

/** Proveedor que abastece libros a la librería. */
public class Proveedor {
    private Integer id;
    private String nombre;
    private String telefono;
    private String correo;

    public Proveedor(Integer id, String nombre, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " | Tel: " + telefono + " | " + correo;
    }
}

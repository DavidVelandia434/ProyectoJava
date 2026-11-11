/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Cliente.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.model;

/** Cliente que realiza compras en la librería. */
public class Cliente {
    private Integer id;
    private String nombre;
    private String documento;
    private String telefono;
    private String correo;

    public Cliente(Integer id, String nombre, String documento, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " | Doc: " + documento + " | Tel: " + telefono + " | " + correo;
    }
}

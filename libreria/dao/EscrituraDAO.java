/*
 * Programa : Sistema de gestión de librería
 * Archivo  : EscrituraDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

/** ISP: contrato solo de escritura (alta, modificación y baja). */
public interface EscrituraDAO<T, ID> {
    /** Inserta la entidad, le asigna su id y la devuelve. */
    T insertar(T entidad);

    /** @return true si existía y se actualizó; false si no se encontró. */
    boolean actualizar(T entidad);

    /** @return true si existía y se eliminó; false si no se encontró. */
    boolean eliminar(ID id);
}

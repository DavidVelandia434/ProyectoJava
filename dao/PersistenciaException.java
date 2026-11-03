/*
 * Programa : Sistema de gestión de librería
 * Archivo  : PersistenciaException.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

/** Error de la capa de persistencia (conexión, SQL o integridad). El mensaje no debe exponer datos sensibles. */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

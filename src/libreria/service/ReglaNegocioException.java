/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ReglaNegocioException.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.service;

/** Se lanza cuando una operación incumple una regla del negocio o una validación. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}

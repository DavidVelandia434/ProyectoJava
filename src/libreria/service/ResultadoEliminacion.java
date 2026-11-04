/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ResultadoEliminacion.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.service;

/** Resultado informado al usuario tras intentar eliminar un registro. */
public enum ResultadoEliminacion {
    ELIMINADO("El registro fue eliminado correctamente."),
    NO_ENCONTRADO("No se encontró ningún registro con ese identificador."),
    RECHAZADO_POR_RESTRICCION("No se puede eliminar: el registro tiene información relacionada.");

    private final String mensaje;

    ResultadoEliminacion(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}

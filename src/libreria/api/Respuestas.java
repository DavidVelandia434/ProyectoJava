/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Respuestas.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import libreria.service.ResultadoEliminacion;

/** Traduce el resultado de una eliminación a un código HTTP. */
public final class Respuestas {
    private Respuestas() { }

    public static ResponseEntity<?> eliminacion(ResultadoEliminacion r) {
        switch (r) {
            case ELIMINADO:
                return ResponseEntity.noContent().build();                                   // 204
            case NO_ENCONTRADO:
                return ResponseEntity.status(404).body(Map.of("error", r.getMensaje()));     // 404
            default:
                return ResponseEntity.status(409).body(Map.of("error", r.getMensaje()));     // 409
        }
    }
}

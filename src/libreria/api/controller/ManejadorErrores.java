/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ManejadorErrores.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import libreria.dao.PersistenciaException;
import libreria.service.ReglaNegocioException;

/** Mensajes de error claros y sin información técnica sensible (requerimiento no funcional). */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(PersistenciaException.class)
    public ResponseEntity<Map<String, String>> persistencia(PersistenciaException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.put(f.getField(), f.getDefaultMessage()));
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("error", "Los datos enviados no son válidos.");
        cuerpo.put("campos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> peticionInvalida(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "La petición tiene un formato inválido."));
    }

    /** Último recurso: no se expone el detalle técnico al cliente. */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> inesperado(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ocurrió un error inesperado."));
    }
}

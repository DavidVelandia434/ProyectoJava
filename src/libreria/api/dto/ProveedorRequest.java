/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ProveedorRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(@NotBlank String nombre, @NotBlank String telefono, @NotBlank String correo) {
}

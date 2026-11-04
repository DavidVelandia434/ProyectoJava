/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ClienteRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Para crear un cliente se usan los 4 campos; para actualizar contacto se ignora 'documento'. */
public record ClienteRequest(@NotBlank String nombre, String documento,
                             @NotBlank String telefono, @NotBlank String correo) {
}

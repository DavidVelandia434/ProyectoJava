/*
 * Programa : Sistema de gestión de librería
 * Archivo  : TituloRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TituloRequest(@NotBlank String titulo) {
}

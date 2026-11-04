/*
 * Programa : Sistema de gestión de librería
 * Archivo  : StockRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Ajuste autorizado de existencias: exige un motivo. */
public record StockRequest(@NotNull @PositiveOrZero Integer stock, @NotBlank String motivo) {
}

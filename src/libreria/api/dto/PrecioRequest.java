/*
 * Programa : Sistema de gestión de librería
 * Archivo  : PrecioRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PrecioRequest(@NotNull @Positive BigDecimal precio) {
}

/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibroRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

public record LibroRequest(
        @NotBlank String isbn,
        @NotBlank String titulo,
        @NotNull @Positive BigDecimal precio,
        @NotNull @PositiveOrZero Integer stock,
        @NotNull Integer idCategoria,
        @NotNull Integer idProveedor,
        @NotEmpty List<Integer> idsAutores) {
}

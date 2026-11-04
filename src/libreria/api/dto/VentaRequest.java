/*
 * Programa : Sistema de gestión de librería
 * Archivo  : VentaRequest.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record VentaRequest(@NotNull Integer idCliente, @NotEmpty List<@Valid ItemVenta> items) {

    public record ItemVenta(@NotNull Integer idLibro, @NotNull @Positive Integer cantidad) {
    }
}

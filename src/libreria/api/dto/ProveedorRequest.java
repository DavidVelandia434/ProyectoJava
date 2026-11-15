package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(@NotBlank String nombre, @NotBlank String telefono, @NotBlank String correo) {}

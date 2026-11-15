package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(@NotBlank String nombre, @NotBlank String documento,
                             @NotBlank String telefono, @NotBlank String correo) {}

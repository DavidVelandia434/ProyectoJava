package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AutorRequest(@NotBlank String nombre, @NotBlank String nacionalidad) {}

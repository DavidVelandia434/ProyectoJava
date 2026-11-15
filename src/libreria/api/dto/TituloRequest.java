package libreria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TituloRequest(@NotBlank String titulo) {}

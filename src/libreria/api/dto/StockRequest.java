package libreria.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StockRequest(@Min(0) int stock, @NotBlank String motivo) {}

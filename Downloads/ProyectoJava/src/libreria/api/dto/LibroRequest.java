package libreria.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record LibroRequest(@NotBlank String isbn, @NotBlank String titulo,
                           @NotNull @Positive BigDecimal precio, @Min(0) int stock,
                           int idCategoria, int idProveedor, @NotEmpty List<Integer> idsAutores) {}

package libreria.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PrecioRequest(@NotNull @Positive BigDecimal precio) {}

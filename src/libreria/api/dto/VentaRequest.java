package libreria.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record VentaRequest(int idCliente, @NotEmpty List<ItemVenta> items) {
    public record ItemVenta(int idLibro, @Min(1) int cantidad) {}
}

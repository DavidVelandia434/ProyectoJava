package libreria.service;

import java.math.BigDecimal;
import libreria.model.Venta;

public class SinDescuento implements PoliticaDescuento {
    @Override
    public BigDecimal calcular(Venta venta) {
        return BigDecimal.ZERO;
    }

    @Override
    public String descripcion() {
        return "Sin descuento";
    }
}

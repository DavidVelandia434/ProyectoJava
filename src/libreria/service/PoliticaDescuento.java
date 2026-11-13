package libreria.service;

import java.math.BigDecimal;
import libreria.model.Venta;

public interface PoliticaDescuento {
    BigDecimal calcular(Venta venta);
    String descripcion();
}

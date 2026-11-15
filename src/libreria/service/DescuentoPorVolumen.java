package libreria.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import libreria.model.Venta;

public class DescuentoPorVolumen implements PoliticaDescuento {
    private final int unidadesMinimas;
    private final BigDecimal porcentaje;

    public DescuentoPorVolumen(int unidadesMinimas, BigDecimal porcentaje) {
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1.");
        }
        this.unidadesMinimas = unidadesMinimas;
        this.porcentaje = porcentaje;
    }

    @Override
    public BigDecimal calcular(Venta venta) {
        if (venta.getTotalUnidades() < unidadesMinimas) {
            return BigDecimal.ZERO;
        }
        return venta.getSubtotal().multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String descripcion() {
        return porcentaje.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString()
                + "% de descuento desde " + unidadesMinimas + " unidades";
    }
}

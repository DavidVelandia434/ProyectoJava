/*
 * Programa : Sistema de gestión de librería
 * Archivo  : SinDescuento.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
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

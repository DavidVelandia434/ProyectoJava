/*
 * Programa : Sistema de gestión de librería
 * Archivo  : PoliticaDescuento.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.service;

import java.math.BigDecimal;
import libreria.model.Venta;

/**
 * Patrón STRATEGY + principio OCP: nuevas políticas = nuevas clases, sin tocar VentaService.
 *
 * CONTRATO (LSP) que toda implementación debe respetar:
 *  - Nunca devuelve null.
 *  - El descuento devuelto es >= 0 y <= subtotal de la venta.
 *  - No modifica la venta recibida.
 */
public interface PoliticaDescuento {
    BigDecimal calcular(Venta venta);

    String descripcion();
}

/*
 * Programa : Sistema de gestión de libreria
 * Archivo  : DetalleVenta.java
 * Fecha    : 02/11/2026
 */
package libreria.model;

import java.math.BigDecimal;

/**
 * Linea de una venta. Guarda el precio unitario del momento de la venta
 * para conservar la integridad histórica aunque el precio del libro cambie.
 */
public class DetalleVenta {
    private Integer id;
    private final int idLibro;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    public DetalleVenta(Integer id, int idLibro, int cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.idLibro = idLibro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getIdLibro() { return idLibro; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }

    /** Subtotal de la linea = cantidad x precio unitario. */
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}

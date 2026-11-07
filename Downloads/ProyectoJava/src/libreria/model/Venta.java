/*
 * Programa : Sistema de gestión de libreria
 * Archivo  : Venta.java
 * Fecha    : 02/11/2026
 */
package libreria.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Venta con sus detalles. Una venta confirmada no se modifica ni se elimina. */
public class Venta {
    private Integer id;
    private final int idCliente;
    private final LocalDate fecha;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private BigDecimal descuento = BigDecimal.ZERO;

    public Venta(Integer id, int idCliente, LocalDate fecha) {
        this.id = id;
        this.idCliente = idCliente;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getIdCliente() { return idCliente; }
    public LocalDate getFecha() { return fecha; }
    public List<DetalleVenta> getDetalles() { return new ArrayList<>(detalles); }
    public void agregarDetalle(DetalleVenta detalle) { detalles.add(detalle); }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getSubtotal() {
        return detalles.stream().map(DetalleVenta::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        return getSubtotal().subtract(descuento);
    }

    public int getTotalUnidades() {
        return detalles.stream().mapToInt(DetalleVenta::getCantidad).sum();
    }

    @Override
    public String toString() {
        return "Venta #" + id + " | Cliente: " + idCliente + " | Fecha: " + fecha
                + " | Unidades: " + getTotalUnidades() + " | Total: $" + getTotal();
    }
}

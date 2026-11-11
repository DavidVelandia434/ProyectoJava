/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MenuVentas.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import libreria.dao.PersistenciaException;
import libreria.model.DetalleVenta;
import libreria.model.Venta;
import libreria.service.ClienteService;
import libreria.service.LibroService;
import libreria.service.ReglaNegocioException;
import libreria.service.VentaService;

public class MenuVentas {
    private final ConsolaUtil c;
    private final VentaService ventas;
    private final LibroService libros;
    private final ClienteService clientes;

    public MenuVentas(ConsolaUtil c, VentaService ventas, LibroService libros, ClienteService clientes) {
        this.c = c;
        this.ventas = ventas;
        this.libros = libros;
        this.clientes = clientes;
    }

    public void ejecutar() {
        int op;
        do {
            ConsolaUtil.titulo("VENTAS");
            System.out.println("1. Registrar venta\n2. Historial por cliente\n3. Historial por fechas\n"
                    + "4. Listar todas\n0. Volver");
            op = c.leerEntero("Opción:");
            try {
                switch (op) {
                    case 1: registrarVenta(); break;
                    case 2:
                        ConsolaUtil.imprimirLista(ventas.historialPorCliente(c.leerEntero("Id del cliente:")));
                        break;
                    case 3:
                        ConsolaUtil.imprimirLista(ventas.historialPorFechas(
                                c.leerFecha("Fecha inicial"), c.leerFecha("Fecha final")));
                        break;
                    case 4: ConsolaUtil.imprimirLista(ventas.listarTodas()); break;
                    case 0: break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (ReglaNegocioException | PersistenciaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    private void registrarVenta() {
        ConsolaUtil.imprimirLista(clientes.listar());
        int idCliente = c.leerEntero("Id del cliente:");
        Map<Integer, Integer> items = new LinkedHashMap<>();
        ConsolaUtil.imprimirLista(libros.listarPorTitulo());
        do {
            int idLibro = c.leerEntero("Id del libro:");
            int cantidad = c.leerEntero("Cantidad:");
            items.merge(idLibro, cantidad, Integer::sum);
        } while (c.confirmar("¿Agregar otro libro?"));
        System.out.println("Política de descuento: " + ventas.descripcionPolitica());
        if (!c.confirmar("¿Confirmar la venta?")) {
            System.out.println("Venta cancelada. No se modificó nada.");
            return;
        }
        imprimirComprobante(ventas.registrarVenta(idCliente, items));
    }

    private void imprimirComprobante(Venta v) {
        ConsolaUtil.titulo("COMPROBANTE DE VENTA #" + v.getId());
        System.out.println("Fecha: " + v.getFecha() + " | Cliente: " + v.getIdCliente());
        for (DetalleVenta d : v.getDetalles()) {
            String titulo = libros.buscarPorId(d.getIdLibro()).map(l -> l.getTitulo()).orElse("?");
            System.out.println("  " + d.getCantidad() + " x " + titulo + " @ $" + d.getPrecioUnitario()
                    + " = $" + d.getSubtotal());
        }
        System.out.println("Subtotal: $" + v.getSubtotal());
        System.out.println("Descuento: $" + v.getDescuento());
        System.out.println("TOTAL: $" + v.getTotal());
    }
}

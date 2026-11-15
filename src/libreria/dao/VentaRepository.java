package libreria.dao;

import java.time.LocalDate;
import java.util.List;
import libreria.model.Venta;

public interface VentaRepository extends LecturaDAO<Venta, Integer> {
    Venta guardarConDetalles(Venta venta);
    List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta);
    List<Venta> listarPorCliente(int idCliente);
    boolean existeVentaConLibro(int idLibro);
    boolean existeVentaConCliente(int idCliente);
}

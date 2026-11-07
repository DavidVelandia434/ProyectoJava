package libreria.dao.memoria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import libreria.dao.LibroDAO;
import libreria.dao.PersistenciaException;
import libreria.dao.VentaRepository;
import libreria.database.BaseDatosEnMemoria;
import libreria.model.DetalleVenta;
import libreria.model.Libro;
import libreria.model.Venta;

public class VentaRepositoryMemoria implements VentaRepository {
    private final BaseDatosEnMemoria bd;
    private final LibroDAO libroDAO;

    public VentaRepositoryMemoria(BaseDatosEnMemoria bd, LibroDAO libroDAO) {
        this.bd = bd;
        this.libroDAO = libroDAO;
    }

    @Override
    public Venta guardarConDetalles(Venta venta) {
        Map<Integer, Integer> pedidoPorLibro = new HashMap<>();
        for (DetalleVenta d : venta.getDetalles()) {
            pedidoPorLibro.merge(d.getIdLibro(), d.getCantidad(), Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : pedidoPorLibro.entrySet()) {
            Libro libro = libroDAO.buscarPorId(e.getKey())
                    .orElseThrow(() -> new PersistenciaException("El libro " + e.getKey() + " no existe."));
            if (e.getValue() > libro.getStock()) {
                throw new PersistenciaException("Existencias insuficientes para el libro " + libro.getId()
                        + ". La venta no se registró.");
            }
        }
        for (Map.Entry<Integer, Integer> e : pedidoPorLibro.entrySet()) {
            Libro libro = libroDAO.buscarPorId(e.getKey()).get();
            libroDAO.actualizarStock(libro.getId(), libro.getStock() - e.getValue());
        }
        venta.setId(bd.ventas.siguienteId());
        for (DetalleVenta d : venta.getDetalles()) {
            d.setId(bd.secuenciaDetalles.siguienteId());
        }
        bd.ventas.filas().put(venta.getId(), venta);
        return venta;
    }

    @Override
    public Optional<Venta> buscarPorId(Integer id) {
        return Optional.ofNullable(bd.ventas.filas().get(id));
    }

    @Override
    public List<Venta> listarTodos() {
        return new ArrayList<>(bd.ventas.filas().values());
    }

    @Override
    public List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta) {
        return bd.ventas.filas().values().stream()
                .filter(v -> !v.getFecha().isBefore(desde) && !v.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    @Override
    public List<Venta> listarPorCliente(int idCliente) {
        return bd.ventas.filas().values().stream()
                .filter(v -> v.getIdCliente() == idCliente)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeVentaConLibro(int idLibro) {
        return bd.ventas.filas().values().stream()
                .anyMatch(v -> v.getDetalles().stream().anyMatch(d -> d.getIdLibro() == idLibro));
    }

    @Override
    public boolean existeVentaConCliente(int idCliente) {
        return bd.ventas.filas().values().stream().anyMatch(v -> v.getIdCliente() == idCliente);
    }
}

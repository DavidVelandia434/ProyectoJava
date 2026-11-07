package libreria.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import libreria.dao.ClienteDAO;
import libreria.dao.LibroDAO;
import libreria.dao.VentaRepository;
import libreria.model.DetalleVenta;
import libreria.model.Libro;
import libreria.model.Venta;

public class VentaService {
    private final VentaRepository ventaRepository;
    private final LibroDAO libroDAO;
    private final ClienteDAO clienteDAO;
    private final PoliticaDescuento politicaDescuento;

    public VentaService(VentaRepository ventaRepository, LibroDAO libroDAO, ClienteDAO clienteDAO,
                        PoliticaDescuento politicaDescuento) {
        this.ventaRepository = ventaRepository;
        this.libroDAO = libroDAO;
        this.clienteDAO = clienteDAO;
        this.politicaDescuento = politicaDescuento;
    }

    public String descripcionPolitica() { return politicaDescuento.descripcion(); }

    public Venta registrarVenta(int idCliente, Map<Integer, Integer> cantidadesPorLibro) {
        if (clienteDAO.buscarPorId(idCliente).isEmpty()) {
            throw new ReglaNegocioException("El cliente indicado no existe.");
        }
        if (cantidadesPorLibro == null || cantidadesPorLibro.isEmpty()) {
            throw new ReglaNegocioException("La venta debe incluir al menos un libro.");
        }
        Venta venta = new Venta(null, idCliente, LocalDate.now());
        for (Map.Entry<Integer, Integer> item : cantidadesPorLibro.entrySet()) {
            int cantidad = item.getValue();
            if (cantidad <= 0) {
                throw new ReglaNegocioException("La cantidad debe ser mayor que cero.");
            }
            Libro libro = libroDAO.buscarPorId(item.getKey())
                    .orElseThrow(() -> new ReglaNegocioException("El libro con id " + item.getKey() + " no existe."));
            if (cantidad > libro.getStock()) {
                throw new ReglaNegocioException("Existencias insuficientes de \"" + libro.getTitulo()
                        + "\": disponibles " + libro.getStock() + ", solicitadas " + cantidad + ".");
            }
            venta.agregarDetalle(new DetalleVenta(null, libro.getId(), cantidad, libro.getPrecio()));
        }
        BigDecimal descuento = politicaDescuento.calcular(venta);
        if (descuento == null || descuento.signum() < 0 || descuento.compareTo(venta.getSubtotal()) > 0) {
            throw new ReglaNegocioException("La política de descuento devolvió un valor inválido.");
        }
        venta.setDescuento(descuento);
        return ventaRepository.guardarConDetalles(venta);
    }

    public Optional<Venta> buscarPorId(int id) { return ventaRepository.buscarPorId(id); }
    public List<Venta> listarTodas() { return ventaRepository.listarTodos(); }
    public List<Venta> historialPorCliente(int idCliente) { return ventaRepository.listarPorCliente(idCliente); }

    public List<Venta> historialPorFechas(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha inicial no puede ser posterior a la final.");
        }
        return ventaRepository.listarPorFecha(desde, hasta);
    }
}

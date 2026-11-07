package libreria.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import libreria.dao.CategoriaDAO;
import libreria.dao.ClienteDAO;
import libreria.dao.LibroDAO;
import libreria.dao.VentaRepository;
import libreria.model.Categoria;
import libreria.model.Cliente;
import libreria.model.DetalleVenta;
import libreria.model.Libro;
import libreria.model.Venta;

public class ReporteService {
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private final ClienteDAO clienteDAO;
    private final VentaRepository ventaRepository;

    public ReporteService(LibroDAO libroDAO, CategoriaDAO categoriaDAO, ClienteDAO clienteDAO,
                          VentaRepository ventaRepository) {
        this.libroDAO = libroDAO;
        this.categoriaDAO = categoriaDAO;
        this.clienteDAO = clienteDAO;
        this.ventaRepository = ventaRepository;
    }

    private Map<Integer, Integer> unidadesPorIdLibro() {
        Map<Integer, Integer> unidades = new HashMap<>();
        for (Venta v : ventaRepository.listarTodos()) {
            for (DetalleVenta d : v.getDetalles()) {
                unidades.merge(d.getIdLibro(), d.getCantidad(), Integer::sum);
            }
        }
        return unidades;
    }

    private String titulo(int idLibro) {
        return libroDAO.buscarPorId(idLibro).map(Libro::getTitulo).orElse("Libro " + idLibro);
    }

    public Map<String, Integer> librosMasVendidos(int n) {
        return unidadesPorIdLibro().entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toMap(e -> titulo(e.getKey()), Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public Map<String, Long> numeroVentasPorCliente() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Cliente c : clienteDAO.listarTodos()) {
            resultado.put(c.getNombre(), (long) ventaRepository.listarPorCliente(c.getId()).size());
        }
        return resultado;
    }

    public Map<LocalDate, BigDecimal> totalVendidoPorDia() {
        Map<LocalDate, BigDecimal> r = new TreeMap<>();
        for (Venta v : ventaRepository.listarTodos()) {
            r.merge(v.getFecha(), v.getTotal(), BigDecimal::add);
        }
        return r;
    }

    public Map<YearMonth, BigDecimal> totalVendidoPorMes() {
        Map<YearMonth, BigDecimal> r = new TreeMap<>();
        for (Venta v : ventaRepository.listarTodos()) {
            r.merge(YearMonth.from(v.getFecha()), v.getTotal(), BigDecimal::add);
        }
        return r;
    }

    public Map<String, Long> cantidadLibrosPorCategoria() {
        Map<String, Long> r = new LinkedHashMap<>();
        List<Libro> libros = libroDAO.listarTodos();
        for (Categoria c : categoriaDAO.listarTodos()) {
            r.put(c.getNombre(), libros.stream()
                    .filter(l -> c.getId().equals(l.getIdCategoria())).count());
        }
        return r;
    }

    public Map<String, Long> categoriasConMasDe(int n) {
        return cantidadLibrosPorCategoria().entrySet().stream()
                .filter(e -> e.getValue() > n)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<String, BigDecimal> precioPromedioPorCategoria() {
        Map<String, BigDecimal> r = new LinkedHashMap<>();
        List<Libro> libros = libroDAO.listarTodos();
        for (Categoria c : categoriaDAO.listarTodos()) {
            List<Libro> deCategoria = libros.stream()
                    .filter(l -> c.getId().equals(l.getIdCategoria())).collect(Collectors.toList());
            if (!deCategoria.isEmpty()) {
                BigDecimal suma = deCategoria.stream().map(Libro::getPrecio).reduce(BigDecimal.ZERO, BigDecimal::add);
                r.put(c.getNombre(), suma.divide(BigDecimal.valueOf(deCategoria.size()), 2, RoundingMode.HALF_UP));
            }
        }
        return r;
    }

    public Optional<Libro> libroMasCaro() {
        return libroDAO.listarTodos().stream().max(Comparator.comparing(Libro::getPrecio));
    }

    public Optional<Libro> libroMasBarato() {
        return libroDAO.listarTodos().stream().min(Comparator.comparing(Libro::getPrecio));
    }

    public List<Libro> librosNuncaVendidos() {
        Map<Integer, Integer> vendidos = unidadesPorIdLibro();
        return libroDAO.listarTodos().stream()
                .filter(l -> !vendidos.containsKey(l.getId()))
                .collect(Collectors.toList());
    }

    public Map<String, Integer> librosSobrePromedioDeVentas() {
        Map<Integer, Integer> unidades = unidadesPorIdLibro();
        if (unidades.isEmpty()) return new LinkedHashMap<>();
        double promedio = unidades.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        List<Map.Entry<Integer, Integer>> sobre = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : unidades.entrySet()) {
            if (e.getValue() > promedio) sobre.add(e);
        }
        return sobre.stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(e -> titulo(e.getKey()), Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public double promedioUnidadesPorLibroVendido() {
        return unidadesPorIdLibro().values().stream().mapToInt(Integer::intValue).average().orElse(0);
    }
}

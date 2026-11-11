/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ReporteService.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
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

/**
 * Emula las consultas de análisis (agregaciones) con streams.
 * Cuando exista la base de datos, cada método se reemplaza por su consulta SQL
 * (GROUP BY, HAVING, LEFT JOIN, subconsultas) en el DAO correspondiente.
 */
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

    /** Consulta 10 / RF08: cantidad vendida por libro, de mayor a menor (limitado a los primeros n). */
    public Map<String, Integer> librosMasVendidos(int n) {
        return unidadesPorIdLibro().entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toMap(e -> titulo(e.getKey()), Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    /** Consulta 8: número de ventas por cliente (incluye clientes sin compras: LEFT JOIN). */
    public Map<String, Long> numeroVentasPorCliente() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Cliente c : clienteDAO.listarTodos()) {
            resultado.put(c.getNombre(), (long) ventaRepository.listarPorCliente(c.getId()).size());
        }
        return resultado;
    }

    /** Consulta 9: total vendido por día. */
    public Map<LocalDate, BigDecimal> totalVendidoPorDia() {
        Map<LocalDate, BigDecimal> r = new TreeMap<>();
        for (Venta v : ventaRepository.listarTodos()) {
            r.merge(v.getFecha(), v.getTotal(), BigDecimal::add);
        }
        return r;
    }

    /** Consulta 9: total vendido por mes. */
    public Map<YearMonth, BigDecimal> totalVendidoPorMes() {
        Map<YearMonth, BigDecimal> r = new TreeMap<>();
        for (Venta v : ventaRepository.listarTodos()) {
            r.merge(YearMonth.from(v.getFecha()), v.getTotal(), BigDecimal::add);
        }
        return r;
    }

    /** Consulta 5: cantidad de libros por categoría. */
    public Map<String, Long> cantidadLibrosPorCategoria() {
        Map<String, Long> r = new LinkedHashMap<>();
        List<Libro> libros = libroDAO.listarTodos();
        for (Categoria c : categoriaDAO.listarTodos()) {
            r.put(c.getNombre(), libros.stream()
                    .filter(l -> c.getId().equals(l.getIdCategoria())).count());
        }
        return r;
    }

    /** Consulta 13: categorías con más de n libros (GROUP BY ... HAVING COUNT(*) > n). */
    public Map<String, Long> categoriasConMasDe(int n) {
        return cantidadLibrosPorCategoria().entrySet().stream()
                .filter(e -> e.getValue() > n)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    /** Consulta 6: precio promedio por categoría. */
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

    /** Consulta 7: libro de mayor precio. */
    public Optional<Libro> libroMasCaro() {
        return libroDAO.listarTodos().stream().max(Comparator.comparing(Libro::getPrecio));
    }

    /** Consulta 7: libro de menor precio. */
    public Optional<Libro> libroMasBarato() {
        return libroDAO.listarTodos().stream().min(Comparator.comparing(Libro::getPrecio));
    }

    /** Consulta 14: libros que nunca han sido vendidos (LEFT JOIN ... WHERE detalle IS NULL). */
    public List<Libro> librosNuncaVendidos() {
        Map<Integer, Integer> vendidos = unidadesPorIdLibro();
        return libroDAO.listarTodos().stream()
                .filter(l -> !vendidos.containsKey(l.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Consulta 15 (subconsulta comparativa): libros cuyas unidades vendidas superan el promedio
     * de unidades vendidas entre los libros que sí se han vendido.
     */
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

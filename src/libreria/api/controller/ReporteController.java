package libreria.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import libreria.model.Libro;
import libreria.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reportes;

    public ReporteController(ReporteService reportes) {
        this.reportes = reportes;
    }

    @GetMapping("/libros-mas-vendidos")
    public Map<String, Integer> masVendidos(@RequestParam(defaultValue = "5") int limite) {
        return reportes.librosMasVendidos(limite);
    }

    @GetMapping("/ventas-por-cliente")
    public Map<String, Long> ventasPorCliente() {
        return reportes.numeroVentasPorCliente();
    }

    @GetMapping("/total-por-dia")
    public Map<LocalDate, BigDecimal> totalPorDia() {
        return reportes.totalVendidoPorDia();
    }

    @GetMapping("/total-por-mes")
    public Map<YearMonth, BigDecimal> totalPorMes() {
        return reportes.totalVendidoPorMes();
    }

    @GetMapping("/libros-por-categoria")
    public Map<String, Long> librosPorCategoria() {
        return reportes.cantidadLibrosPorCategoria();
    }

    @GetMapping("/categorias-con-mas-de")
    public Map<String, Long> categoriasConMasDe(@RequestParam int n) {
        return reportes.categoriasConMasDe(n);
    }

    @GetMapping("/precio-promedio-por-categoria")
    public Map<String, BigDecimal> precioPromedio() {
        return reportes.precioPromedioPorCategoria();
    }

    @GetMapping("/libro-mas-caro")
    public ResponseEntity<Libro> masCaro() {
        return reportes.libroMasCaro().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/libro-mas-barato")
    public ResponseEntity<Libro> masBarato() {
        return reportes.libroMasBarato().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/libros-nunca-vendidos")
    public List<Libro> nuncaVendidos() {
        return reportes.librosNuncaVendidos();
    }

    @GetMapping("/libros-sobre-promedio")
    public Map<String, Integer> sobrePromedio() {
        return reportes.librosSobrePromedioDeVentas();
    }
}

package libreria.api.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import libreria.api.Respuestas;
import libreria.api.dto.LibroRequest;
import libreria.api.dto.PrecioRequest;
import libreria.api.dto.StockRequest;
import libreria.api.dto.TituloRequest;
import libreria.model.Libro;
import libreria.service.LibroService;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService libros;

    public LibroController(LibroService libros) {
        this.libros = libros;
    }

    @GetMapping
    public List<Libro> listar(@RequestParam(required = false) Integer categoria,
                              @RequestParam(required = false) BigDecimal precioMin,
                              @RequestParam(required = false) BigDecimal precioMax,
                              @RequestParam(required = false) Integer stockMenorA) {
        if (categoria != null) return libros.listarPorCategoria(categoria);
        if (precioMin != null && precioMax != null) return libros.listarPorRangoPrecio(precioMin, precioMax);
        if (stockMenorA != null) return libros.listarStockBajo(stockMenorA);
        return libros.listarPorTitulo();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtener(@PathVariable int id) {
        return libros.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Libro> crear(@Valid @RequestBody LibroRequest r) {
        Libro libro = libros.registrar(r.isbn(), r.titulo(), r.precio(), r.stock(),
                r.idCategoria(), r.idProveedor(), r.idsAutores());
        return ResponseEntity.status(HttpStatus.CREATED).body(libro);
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<Libro> actualizarPrecio(@PathVariable int id, @Valid @RequestBody PrecioRequest r) {
        libros.actualizarPrecio(id, r.precio());
        return obtener(id);
    }

    @PutMapping("/{id}/titulo")
    public ResponseEntity<Libro> corregirTitulo(@PathVariable int id, @Valid @RequestBody TituloRequest r) {
        libros.corregirTitulo(id, r.titulo());
        return obtener(id);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Libro> ajustarStock(@PathVariable int id, @Valid @RequestBody StockRequest r) {
        libros.ajustarStock(id, r.stock(), r.motivo());
        return obtener(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        return Respuestas.eliminacion(libros.eliminar(id));
    }
}

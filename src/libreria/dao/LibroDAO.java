package libreria.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import libreria.model.Libro;

public interface LibroDAO extends LecturaDAO<Libro, Integer>, EscrituraDAO<Libro, Integer> {
    Optional<Libro> buscarPorIsbn(String isbn);
    List<Libro> listarPorTitulo();
    List<Libro> listarPorCategoria(int idCategoria);
    List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max);
    List<Libro> listarConStockInferiorA(int umbral);
    boolean actualizarPrecio(int idLibro, BigDecimal nuevoPrecio);
    boolean actualizarTitulo(int idLibro, String nuevoTitulo);
    boolean actualizarStock(int idLibro, int nuevoStock);
}

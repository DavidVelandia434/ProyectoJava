/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibroDAOMemoria.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.memoria;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import libreria.dao.LibroDAO;
import libreria.database.BaseDatosEnMemoria;
import libreria.model.Libro;

public class LibroDAOMemoria extends DAOMemoriaBase<Libro> implements LibroDAO {

    public LibroDAOMemoria(BaseDatosEnMemoria bd) { super(bd.libros); }
    @Override protected Integer obtenerId(Libro l) { return l.getId(); }
    @Override protected void asignarId(Libro l, int id) { l.setId(id); }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return almacen.filas().values().stream().filter(l -> l.getIsbn().equals(isbn)).findFirst();
    }

    @Override
    public List<Libro> listarPorTitulo() {
        // SQL futuro: SELECT * FROM libro ORDER BY titulo
        return almacen.filas().values().stream()
                .sorted(Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public List<Libro> listarPorCategoria(int idCategoria) {
        // SQL futuro: SELECT * FROM libro WHERE id_categoria = ?
        return almacen.filas().values().stream()
                .filter(l -> l.getIdCategoria() != null && l.getIdCategoria() == idCategoria)
                .collect(Collectors.toList());
    }

    @Override
    public List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        // SQL futuro: SELECT * FROM libro WHERE precio BETWEEN ? AND ?
        return almacen.filas().values().stream()
                .filter(l -> l.getPrecio().compareTo(min) >= 0 && l.getPrecio().compareTo(max) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Libro> listarConStockInferiorA(int umbral) {
        // SQL futuro: SELECT * FROM libro WHERE stock < ?
        return almacen.filas().values().stream()
                .filter(l -> l.getStock() < umbral)
                .collect(Collectors.toList());
    }

    @Override
    public boolean actualizarPrecio(int idLibro, BigDecimal nuevoPrecio) {
        Libro l = almacen.filas().get(idLibro);
        if (l == null) return false;
        l.setPrecio(nuevoPrecio);
        return true;
    }

    @Override
    public boolean actualizarTitulo(int idLibro, String nuevoTitulo) {
        Libro l = almacen.filas().get(idLibro);
        if (l == null) return false;
        l.setTitulo(nuevoTitulo);
        return true;
    }

    @Override
    public boolean actualizarStock(int idLibro, int nuevoStock) {
        Libro l = almacen.filas().get(idLibro);
        if (l == null) return false;
        l.setStock(nuevoStock);
        return true;
    }
}

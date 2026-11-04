/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibroDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import libreria.model.Libro;

public interface LibroDAO extends LecturaDAO<Libro, Integer>, EscrituraDAO<Libro, Integer> {

    Optional<Libro> buscarPorIsbn(String isbn);

    /** Consulta 1: libros ordenados por título. */
    List<Libro> listarPorTitulo();

    /** Consulta 2: libros de una categoría. */
    List<Libro> listarPorCategoria(int idCategoria);

    /** Consulta 3: libros cuyo precio esté entre min y max (inclusive). */
    List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max);

    /** Consulta 4: libros con existencias inferiores al umbral. */
    List<Libro> listarConStockInferiorA(int umbral);

    // Actualizaciones diferenciadas (UPDATE 1, 2 y 3)
    boolean actualizarPrecio(int idLibro, BigDecimal nuevoPrecio);

    boolean actualizarTitulo(int idLibro, String nuevoTitulo);

    boolean actualizarStock(int idLibro, int nuevoStock);
}

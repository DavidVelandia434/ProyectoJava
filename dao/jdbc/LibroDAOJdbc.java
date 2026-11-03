/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibroDAOJdbc.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.jdbc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import libreria.dao.LibroDAO;
import libreria.database.ConexionBD;
import libreria.model.Libro;

/**
 * ============================================================
 *  PLANTILLA JDBC (pendiente de implementar)
 * ============================================================
 * Cuando exista la base de datos, implementa cada método con PreparedStatement
 * y try-with-resources, y en Main reemplaza LibroDAOMemoria por esta clase.
 *
 * Ejemplo de patrón a seguir:
 *
 *   String sql = "SELECT id_libro, isbn, titulo, precio, stock FROM libro WHERE id_libro = ?";
 *   try (Connection con = conexionBD.obtenerConexion();
 *        PreparedStatement ps = con.prepareStatement(sql)) {
 *       ps.setInt(1, id);
 *       try (ResultSet rs = ps.executeQuery()) {
 *           if (rs.next()) { return Optional.of(mapear(rs)); }
 *       }
 *   } catch (SQLException e) {
 *       throw new PersistenciaException("No fue posible consultar el libro.", e);
 *   }
 */
public class LibroDAOJdbc implements LibroDAO {
    private final ConexionBD conexionBD;

    public LibroDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    private static UnsupportedOperationException pendiente() {
        return new UnsupportedOperationException("TODO: implementar con JDBC");
    }

    @Override public Optional<Libro> buscarPorId(Integer id) { throw pendiente(); }
    @Override public List<Libro> listarTodos() { throw pendiente(); }
    @Override public Libro insertar(Libro libro) { throw pendiente(); }   // INSERT libro + INSERT libro_autor
    @Override public boolean actualizar(Libro libro) { throw pendiente(); }
    @Override public boolean eliminar(Integer id) { throw pendiente(); }
    @Override public Optional<Libro> buscarPorIsbn(String isbn) { throw pendiente(); }
    @Override public List<Libro> listarPorTitulo() { throw pendiente(); }
    @Override public List<Libro> listarPorCategoria(int idCategoria) { throw pendiente(); }
    @Override public List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max) { throw pendiente(); }
    @Override public List<Libro> listarConStockInferiorA(int umbral) { throw pendiente(); }
    @Override public boolean actualizarPrecio(int idLibro, BigDecimal nuevoPrecio) { throw pendiente(); }
    @Override public boolean actualizarTitulo(int idLibro, String nuevoTitulo) { throw pendiente(); }
    @Override public boolean actualizarStock(int idLibro, int nuevoStock) { throw pendiente(); }
}

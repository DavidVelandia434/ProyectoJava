/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibroDAOJdbc.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.LibroDAO;
import libreria.dao.PersistenciaException;
import libreria.database.ConexionBD;
import libreria.model.Libro;

/**
 * Implementación JDBC de LibroDAO para PostgreSQL / Supabase.
 *
 * Tablas esperadas:
 *   libro (id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor)
 *   libro_autor (id_libro, id_autor)
 */
public class LibroDAOJdbc implements LibroDAO {

    private final ConexionBD conexionBD;

    public LibroDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id_libro"),
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock"),
                (Integer) rs.getObject("id_categoria"),
                (Integer) rs.getObject("id_proveedor"),
                new ArrayList<>()
        );
    }

    private List<Integer> cargarAutores(Connection con, int idLibro) throws SQLException {
        List<Integer> autores = new ArrayList<>();
        String sql = "SELECT id_autor FROM libro_autor WHERE id_libro = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    autores.add(rs.getInt("id_autor"));
                }
            }
        }
        return autores;
    }

    @Override
    public Optional<Libro> buscarPorId(Integer id) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor "
                   + "FROM libro WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro libro = mapear(rs);
                    libro.setIdsAutores(cargarAutores(con, id));
                    return Optional.of(libro);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el libro.", e);
        }
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor "
                   + "FROM libro WHERE isbn = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro libro = mapear(rs);
                    libro.setIdsAutores(cargarAutores(con, libro.getId()));
                    return Optional.of(libro);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el libro por ISBN.", e);
        }
    }

    @Override
    public List<Libro> listarTodos() {
        return listarPorTitulo();
    }

    @Override
    public List<Libro> listarPorTitulo() {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor "
                   + "FROM libro ORDER BY titulo";
        List<Libro> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Libro libro = mapear(rs);
                libro.setIdsAutores(cargarAutores(con, libro.getId()));
                lista.add(libro);
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar los libros.", e);
        }
    }

    @Override
    public List<Libro> listarPorCategoria(int idCategoria) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor "
                   + "FROM libro WHERE id_categoria = ? ORDER BY titulo";
        List<Libro> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapear(rs);
                    libro.setIdsAutores(cargarAutores(con, libro.getId()));
                    lista.add(libro);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar libros por categoría.", e);
        }
    }

    @Override
    public List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor "
                   + "FROM libro WHERE precio BETWEEN ? AND ? ORDER BY precio";
        List<Libro> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapear(rs);
                    libro.setIdsAutores(cargarAutores(con, libro.getId()));
                    lista.add(libro);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar libros por rango de precio.", e);
        }
    }

    @Override
    public List<Libro> listarConStockInferiorA(int umbral) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor "
                   + "FROM libro WHERE stock < ? ORDER BY stock";
        List<Libro> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, umbral);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro libro = mapear(rs);
                    libro.setIdsAutores(cargarAutores(con, libro.getId()));
                    lista.add(libro);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar libros con stock bajo.", e);
        }
    }

    @Override
    public Libro insertar(Libro libro) {
        String sqlLibro = "INSERT INTO libro (isbn, titulo, precio, stock, id_categoria, id_proveedor) "
                        + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id_libro";
        String sqlAutor = "INSERT INTO libro_autor (id_libro, id_autor) VALUES (?, ?)";

        try (Connection con = conexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlLibro)) {
                    ps.setString(1, libro.getIsbn());
                    ps.setString(2, libro.getTitulo());
                    ps.setBigDecimal(3, libro.getPrecio());
                    ps.setInt(4, libro.getStock());
                    if (libro.getIdCategoria() != null) {
                        ps.setInt(5, libro.getIdCategoria());
                    } else {
                        ps.setNull(5, java.sql.Types.INTEGER);
                    }
                    if (libro.getIdProveedor() != null) {
                        ps.setInt(6, libro.getIdProveedor());
                    } else {
                        ps.setNull(6, java.sql.Types.INTEGER);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            libro.setId(rs.getInt(1));
                        } else {
                            throw new PersistenciaException("No se pudo obtener el id del libro insertado.");
                        }
                    }
                }

                if (libro.getIdsAutores() != null && !libro.getIdsAutores().isEmpty()) {
                    try (PreparedStatement ps = con.prepareStatement(sqlAutor)) {
                        for (Integer idAutor : libro.getIdsAutores()) {
                            ps.setInt(1, libro.getId());
                            ps.setInt(2, idAutor);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                con.commit();
                return libro;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw new PersistenciaException("No fue posible insertar el libro.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible insertar el libro.", e);
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libro SET isbn = ?, titulo = ?, precio = ?, stock = ?, "
                   + "id_categoria = ?, id_proveedor = ? WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setBigDecimal(3, libro.getPrecio());
            ps.setInt(4, libro.getStock());
            if (libro.getIdCategoria() != null) {
                ps.setInt(5, libro.getIdCategoria());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            if (libro.getIdProveedor() != null) {
                ps.setInt(6, libro.getIdProveedor());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setInt(7, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar el libro.", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM libro WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible eliminar el libro.", e);
        }
    }

    @Override
    public boolean actualizarPrecio(int idLibro, BigDecimal nuevoPrecio) {
        String sql = "UPDATE libro SET precio = ? WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoPrecio);
            ps.setInt(2, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar el precio.", e);
        }
    }

    @Override
    public boolean actualizarTitulo(int idLibro, String nuevoTitulo) {
        String sql = "UPDATE libro SET titulo = ? WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoTitulo);
            ps.setInt(2, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar el título.", e);
        }
    }

    @Override
    public boolean actualizarStock(int idLibro, int nuevoStock) {
        String sql = "UPDATE libro SET stock = ? WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idLibro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar el stock.", e);
        }
    }
}
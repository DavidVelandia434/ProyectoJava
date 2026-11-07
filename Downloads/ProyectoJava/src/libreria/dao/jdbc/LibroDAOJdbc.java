package libreria.dao.jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.LibroDAO;
import libreria.dao.PersistenciaException;
import libreria.database.ConexionBD;
import libreria.model.Libro;

public class LibroDAOJdbc implements LibroDAO {
    private final ConexionBD conexionBD;

    public LibroDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Libro> buscarPorId(Integer id) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(con, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el libro.", e);
        }
    }

    @Override
    public List<Libro> listarTodos() {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro ORDER BY id_libro";
        return consultarLista(sql, null);
    }

    @Override
    public Libro insertar(Libro libro) {
        String sqlLibro = "INSERT INTO libro (isbn, titulo, precio, stock, id_categoria, id_proveedor) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_libro";
        String sqlAutor = "INSERT INTO libro_autor (id_libro, id_autor) VALUES (?, ?)";
        try (Connection con = conexionBD.obtenerConexion()) {
            try {
                con.setAutoCommit(false);
                try (PreparedStatement ps = con.prepareStatement(sqlLibro)) {
                    ps.setString(1, libro.getIsbn());
                    ps.setString(2, libro.getTitulo());
                    ps.setBigDecimal(3, libro.getPrecio());
                    ps.setInt(4, libro.getStock());
                    ps.setInt(5, libro.getIdCategoria());
                    ps.setInt(6, libro.getIdProveedor());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            libro.setId(rs.getInt(1));
                        }
                    }
                }
                try (PreparedStatement psAutor = con.prepareStatement(sqlAutor)) {
                    for (Integer idAutor : libro.getIdsAutores()) {
                        psAutor.setInt(1, libro.getId());
                        psAutor.setInt(2, idAutor);
                        psAutor.addBatch();
                    }
                    psAutor.executeBatch();
                }
                con.commit();
                return libro;
            } catch (SQLException e) {
                con.rollback();
                throw new PersistenciaException("No fue posible insertar el libro y sus autores.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de conexión al insertar el libro.", e);
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libro SET isbn = ?, titulo = ?, precio = ?, stock = ?, id_categoria = ?, id_proveedor = ? WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setBigDecimal(3, libro.getPrecio());
            ps.setInt(4, libro.getStock());
            ps.setInt(5, libro.getIdCategoria());
            ps.setInt(6, libro.getIdProveedor());
            ps.setInt(7, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar el libro.", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sqlAutores = "DELETE FROM libro_autor WHERE id_libro = ?";
        String sqlLibro = "DELETE FROM libro WHERE id_libro = ?";
        try (Connection con = conexionBD.obtenerConexion()) {
            try {
                con.setAutoCommit(false);
                try (PreparedStatement psA = con.prepareStatement(sqlAutores)) {
                    psA.setInt(1, id);
                    psA.executeUpdate();
                }
                int filas;
                try (PreparedStatement psL = con.prepareStatement(sqlLibro)) {
                    psL.setInt(1, id);
                    filas = psL.executeUpdate();
                }
                con.commit();
                return filas > 0;
            } catch (SQLException e) {
                con.rollback();
                throw new PersistenciaException("No se puede eliminar el libro porque tiene ventas asociadas.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el libro.", e);
        }
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro WHERE isbn = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(con, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible buscar el libro por ISBN.", e);
        }
    }

    @Override
    public List<Libro> listarPorTitulo() {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro ORDER BY LOWER(titulo)";
        return consultarLista(sql, null);
    }

    @Override
    public List<Libro> listarPorCategoria(int idCategoria) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro WHERE id_categoria = ? ORDER BY titulo";
        return consultarLista(sql, ps -> ps.setInt(1, idCategoria));
    }

    @Override
    public List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro WHERE precio BETWEEN ? AND ? ORDER BY precio";
        return consultarLista(sql, ps -> {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
        });
    }

    @Override
    public List<Libro> listarConStockInferiorA(int umbral) {
        String sql = "SELECT id_libro, isbn, titulo, precio, stock, id_categoria, id_proveedor FROM libro WHERE stock < ? ORDER BY stock ASC";
        return consultarLista(sql, ps -> ps.setInt(1, umbral));
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
            throw new PersistenciaException("No fue posible actualizar el precio del libro.", e);
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
            throw new PersistenciaException("No fue posible corregir el título del libro.", e);
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
            throw new PersistenciaException("No fue posible actualizar las existencias del libro.", e);
        }
    }

    @FunctionalInterface
    private interface Parametrizer {
        void apply(PreparedStatement ps) throws SQLException;
    }

    private List<Libro> consultarLista(String sql, Parametrizer param) {
        List<Libro> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                param.apply(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(con, rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el catálogo de libros.", e);
        }
    }

    private Libro mapear(Connection con, ResultSet rs) throws SQLException {
        int idLibro = rs.getInt("id_libro");
        List<Integer> autores = new ArrayList<>();
        try (PreparedStatement psA = con.prepareStatement(
                "SELECT id_autor FROM libro_autor WHERE id_libro = ? ORDER BY id_autor")) {
            psA.setInt(1, idLibro);
            try (ResultSet rsA = psA.executeQuery()) {
                while (rsA.next()) {
                    autores.add(rsA.getInt("id_autor"));
                }
            }
        }
        return new Libro(
            idLibro,
            rs.getString("isbn"),
            rs.getString("titulo"),
            rs.getBigDecimal("precio"),
            rs.getInt("stock"),
            rs.getInt("id_categoria"),
            rs.getInt("id_proveedor"),
            autores
        );
    }
}

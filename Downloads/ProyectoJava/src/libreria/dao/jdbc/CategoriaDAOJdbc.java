package libreria.dao.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.CategoriaDAO;
import libreria.dao.PersistenciaException;
import libreria.database.ConexionBD;
import libreria.model.Categoria;

public class CategoriaDAOJdbc implements CategoriaDAO {
    private final ConexionBD conexionBD;

    public CategoriaDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Categoria> buscarPorId(Integer id) {
        String sql = "SELECT id_categoria, nombre FROM categoria WHERE id_categoria = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar la categoría.", e);
        }
    }

    @Override
    public List<Categoria> listarTodos() {
        String sql = "SELECT id_categoria, nombre FROM categoria ORDER BY id_categoria";
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar las categorías.", e);
        }
    }

    @Override
    public Categoria insertar(Categoria entidad) {
        String sql = "INSERT INTO categoria (nombre) VALUES (?) RETURNING id_categoria";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entidad.setId(rs.getInt(1));
                }
            }
            return entidad;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible registrar la categoría.", e);
        }
    }

    @Override
    public boolean actualizar(Categoria entidad) {
        String sql = "UPDATE categoria SET nombre = ? WHERE id_categoria = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setInt(2, entidad.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar la categoría.", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se puede eliminar la categoría porque tiene libros asociados.", e);
        }
    }
}

package libreria.dao.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.AutorDAO;
import libreria.dao.PersistenciaException;
import libreria.database.ConexionBD;
import libreria.model.Autor;

public class AutorDAOJdbc implements AutorDAO {
    private final ConexionBD conexionBD;

    public AutorDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Autor> buscarPorId(Integer id) {
        String sql = "SELECT id_autor, nombre, nacionalidad FROM autor WHERE id_autor = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Autor(rs.getInt("id_autor"), rs.getString("nombre"), rs.getString("nacionalidad")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el autor.", e);
        }
    }

    @Override
    public List<Autor> listarTodos() {
        String sql = "SELECT id_autor, nombre, nacionalidad FROM autor ORDER BY id_autor";
        List<Autor> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Autor(rs.getInt("id_autor"), rs.getString("nombre"), rs.getString("nacionalidad")));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar los autores.", e);
        }
    }

    @Override
    public Autor insertar(Autor entidad) {
        String sql = "INSERT INTO autor (nombre, nacionalidad) VALUES (?, ?) RETURNING id_autor";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getNacionalidad());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entidad.setId(rs.getInt(1));
                }
            }
            return entidad;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible registrar el autor.", e);
        }
    }

    @Override
    public boolean actualizar(Autor entidad) {
        String sql = "UPDATE autor SET nombre = ?, nacionalidad = ? WHERE id_autor = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getNacionalidad());
            ps.setInt(3, entidad.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar el autor.", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM autor WHERE id_autor = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se puede eliminar el autor porque tiene registros asociados.", e);
        }
    }
}

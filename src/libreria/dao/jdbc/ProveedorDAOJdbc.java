package libreria.dao.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.PersistenciaException;
import libreria.dao.ProveedorDAO;
import libreria.database.ConexionBD;
import libreria.model.Proveedor;

public class ProveedorDAOJdbc implements ProveedorDAO {
    private final ConexionBD conexionBD;

    public ProveedorDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Proveedor> buscarPorId(Integer id) {
        String sql = "SELECT id_proveedor, nombre, telefono, correo FROM proveedor WHERE id_proveedor = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("correo")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el proveedor.", e);
        }
    }

    @Override
    public List<Proveedor> listarTodos() {
        String sql = "SELECT id_proveedor, nombre, telefono, correo FROM proveedor ORDER BY id_proveedor";
        List<Proveedor> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Proveedor(
                    rs.getInt("id_proveedor"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar los proveedores.", e);
        }
    }

    @Override
    public Proveedor insertar(Proveedor entidad) {
        String sql = "INSERT INTO proveedor (nombre, telefono, correo) VALUES (?, ?, ?) RETURNING id_proveedor";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getTelefono());
            ps.setString(3, entidad.getCorreo());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entidad.setId(rs.getInt(1));
                }
            }
            return entidad;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible registrar el proveedor.", e);
        }
    }

    @Override
    public boolean actualizar(Proveedor entidad) {
        String sql = "UPDATE proveedor SET nombre = ?, telefono = ?, correo = ? WHERE id_proveedor = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getTelefono());
            ps.setString(3, entidad.getCorreo());
            ps.setInt(4, entidad.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar los datos del proveedor.", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM proveedor WHERE id_proveedor = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se puede eliminar el proveedor porque tiene libros asociados.", e);
        }
    }
}

package libreria.dao.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.ClienteDAO;
import libreria.dao.PersistenciaException;
import libreria.database.ConexionBD;
import libreria.model.Cliente;

public class ClienteDAOJdbc implements ClienteDAO {
    private final ConexionBD conexionBD;

    public ClienteDAOJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        String sql = "SELECT id_cliente, nombre, documento, telefono, correo FROM cliente WHERE id_cliente = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el cliente.", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        String sql = "SELECT id_cliente, nombre, documento, telefono, correo FROM cliente WHERE documento = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el cliente por documento.", e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT id_cliente, nombre, documento, telefono, correo FROM cliente ORDER BY id_cliente";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar los clientes.", e);
        }
    }

    @Override
    public Cliente insertar(Cliente entidad) {
        String sql = "INSERT INTO cliente (nombre, documento, telefono, correo) VALUES (?, ?, ?, ?) RETURNING id_cliente";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getDocumento());
            ps.setString(3, entidad.getTelefono());
            ps.setString(4, entidad.getCorreo());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entidad.setId(rs.getInt(1));
                }
            }
            return entidad;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible registrar el cliente.", e);
        }
    }

    @Override
    public boolean actualizar(Cliente entidad) {
        String sql = "UPDATE cliente SET nombre = ?, documento = ?, telefono = ?, correo = ? WHERE id_cliente = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getDocumento());
            ps.setString(3, entidad.getTelefono());
            ps.setString(4, entidad.getCorreo());
            ps.setInt(5, entidad.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar los datos del cliente.", e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se puede eliminar el cliente porque tiene ventas registradas.", e);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("nombre"),
            rs.getString("documento"),
            rs.getString("telefono"),
            rs.getString("correo")
        );
    }
}

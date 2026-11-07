package libreria.dao.jdbc;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.PersistenciaException;
import libreria.dao.VentaRepository;
import libreria.database.ConexionBD;
import libreria.model.DetalleVenta;
import libreria.model.Venta;

public class VentaRepositoryJdbc implements VentaRepository {
    private final ConexionBD conexionBD;

    public VentaRepositoryJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Venta guardarConDetalles(Venta venta) {
        String sqlVenta = "INSERT INTO venta (id_cliente, fecha, descuento, total) VALUES (?, ?, ?, ?) RETURNING id_venta";
        String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_libro, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?) RETURNING id_detalle";
        String sqlStock = "UPDATE libro SET stock = stock - ? WHERE id_libro = ? AND stock >= ?";

        try (Connection con = conexionBD.obtenerConexion()) {
            try {
                con.setAutoCommit(false);

                try (PreparedStatement psV = con.prepareStatement(sqlVenta)) {
                    psV.setInt(1, venta.getIdCliente());
                    psV.setDate(2, Date.valueOf(venta.getFecha()));
                    psV.setBigDecimal(3, venta.getDescuento());
                    psV.setBigDecimal(4, venta.getTotal());
                    try (ResultSet rs = psV.executeQuery()) {
                        if (rs.next()) {
                            venta.setId(rs.getInt(1));
                        }
                    }
                }

                for (DetalleVenta d : venta.getDetalles()) {
                    try (PreparedStatement psS = con.prepareStatement(sqlStock)) {
                        psS.setInt(1, d.getCantidad());
                        psS.setInt(2, d.getIdLibro());
                        psS.setInt(3, d.getCantidad());
                        if (psS.executeUpdate() == 0) {
                            throw new PersistenciaException("Stock insuficiente para el libro con ID " + d.getIdLibro());
                        }
                    }
                    try (PreparedStatement psD = con.prepareStatement(sqlDetalle)) {
                        psD.setInt(1, venta.getId());
                        psD.setInt(2, d.getIdLibro());
                        psD.setInt(3, d.getCantidad());
                        psD.setBigDecimal(4, d.getPrecioUnitario());
                        psD.setBigDecimal(5, d.getSubtotal());
                        try (ResultSet rsD = psD.executeQuery()) {
                            if (rsD.next()) {
                                d.setId(rsD.getInt(1));
                            }
                        }
                    }
                }

                con.commit();
                return venta;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                if (e instanceof PersistenciaException) throw (PersistenciaException) e;
                throw new PersistenciaException("No fue posible registrar la venta de forma transaccional.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de conexión al procesar la transacción de venta.", e);
        }
    }

    @Override
    public Optional<Venta> buscarPorId(Integer id) {
        String sql = "SELECT id_venta, id_cliente, fecha, descuento FROM venta WHERE id_venta = ?";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearVenta(con, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar la venta.", e);
        }
    }

    @Override
    public List<Venta> listarTodos() {
        String sql = "SELECT id_venta, id_cliente, fecha, descuento FROM venta ORDER BY fecha DESC, id_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ventas.add(mapearVenta(con, rs));
            }
            return ventas;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible listar las ventas.", e);
        }
    }

    @Override
    public List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT id_venta, id_cliente, fecha, descuento FROM venta WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC, id_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(con, rs));
                }
            }
            return ventas;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar ventas por rango de fechas.", e);
        }
    }

    @Override
    public List<Venta> listarPorCliente(int idCliente) {
        String sql = "SELECT id_venta, id_cliente, fecha, descuento FROM venta WHERE id_cliente = ? ORDER BY fecha DESC, id_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(con, rs));
                }
            }
            return ventas;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible consultar el historial de ventas del cliente.", e);
        }
    }

    @Override
    public boolean existeVentaConLibro(int idLibro) {
        String sql = "SELECT 1 FROM detalle_venta WHERE id_libro = ? LIMIT 1";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible verificar ventas asociadas al libro.", e);
        }
    }

    @Override
    public boolean existeVentaConCliente(int idCliente) {
        String sql = "SELECT 1 FROM venta WHERE id_cliente = ? LIMIT 1";
        try (Connection con = conexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible verificar ventas asociadas al cliente.", e);
        }
    }

    private Venta mapearVenta(Connection con, ResultSet rs) throws SQLException {
        int idVenta = rs.getInt("id_venta");
        Venta v = new Venta(idVenta, rs.getInt("id_cliente"), rs.getDate("fecha").toLocalDate());
        v.setDescuento(rs.getBigDecimal("descuento"));
        String sqlDet = "SELECT id_detalle, id_libro, cantidad, precio_unitario FROM detalle_venta WHERE id_venta = ? ORDER BY id_detalle";
        try (PreparedStatement psD = con.prepareStatement(sqlDet)) {
            psD.setInt(1, idVenta);
            try (ResultSet rsD = psD.executeQuery()) {
                while (rsD.next()) {
                    v.agregarDetalle(new DetalleVenta(
                        rsD.getInt("id_detalle"),
                        rsD.getInt("id_libro"),
                        rsD.getInt("cantidad"),
                        rsD.getBigDecimal("precio_unitario")
                    ));
                }
            }
        }
        return v;
    }
}

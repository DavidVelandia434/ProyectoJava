/*
 * Programa : Sistema de gestión de librería
 * Archivo  : VentaRepositoryJdbc.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.jdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import libreria.dao.VentaRepository;
import libreria.database.ConexionBD;
import libreria.model.Venta;

/**
 * ============================================================
 *  PLANTILLA JDBC (pendiente de implementar)
 * ============================================================
 * guardarConDetalles debe ser UNA transacción:
 *
 *   try (Connection con = conexionBD.obtenerConexion()) {
 *       try {
 *           con.setAutoCommit(false);
 *           // 1. INSERT INTO venta ...            (obtener id generado)
 *           // 2. INSERT INTO detalle_venta ...    (uno por cada detalle)
 *           // 3. UPDATE libro SET stock = stock - ? WHERE id_libro = ? AND stock >= ?
 *           //    (si affectedRows == 0 -> existencias insuficientes -> lanzar excepción)
 *           con.commit();
 *       } catch (SQLException | RuntimeException e) {
 *           con.rollback();
 *           throw new PersistenciaException("No fue posible registrar la venta.", e);
 *       }
 *   }
 */
public class VentaRepositoryJdbc implements VentaRepository {
    private final ConexionBD conexionBD;

    public VentaRepositoryJdbc(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    private static UnsupportedOperationException pendiente() {
        return new UnsupportedOperationException("TODO: implementar con JDBC");
    }

    @Override public Venta guardarConDetalles(Venta venta) { throw pendiente(); }
    @Override public Optional<Venta> buscarPorId(Integer id) { throw pendiente(); }
    @Override public List<Venta> listarTodos() { throw pendiente(); }
    @Override public List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta) { throw pendiente(); }
    @Override public List<Venta> listarPorCliente(int idCliente) { throw pendiente(); }
    @Override public boolean existeVentaConLibro(int idLibro) { throw pendiente(); }
    @Override public boolean existeVentaConCliente(int idCliente) { throw pendiente(); }
}

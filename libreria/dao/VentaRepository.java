/*
 * Programa : Sistema de gestión de librería
 * Archivo  : VentaRepository.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import java.time.LocalDate;
import java.util.List;
import libreria.model.Venta;

/**
 * DIP: VentaService depende de esta abstracción y no de una clase JDBC concreta.
 * ISP: solo extiende LecturaDAO, porque una venta confirmada no se actualiza ni se elimina.
 */
public interface VentaRepository extends LecturaDAO<Venta, Integer> {

    /**
     * Guarda la venta con todos sus detalles y descuenta el stock de cada libro
     * de forma ATÓMICA (todo o nada).
     * Versión JDBC: setAutoCommit(false), INSERT venta, INSERT detalles, UPDATE libro SET stock = stock - ?,
     * commit; y rollback ante cualquier error.
     *
     * @throws PersistenciaException si falla algún paso o no hay existencias suficientes
     */
    Venta guardarConDetalles(Venta venta);

    List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta);

    List<Venta> listarPorCliente(int idCliente);

    boolean existeVentaConLibro(int idLibro);

    boolean existeVentaConCliente(int idCliente);
}

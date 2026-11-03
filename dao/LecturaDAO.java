/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LecturaDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import java.util.List;
import java.util.Optional;

/** ISP: contrato solo de lectura. Lo usan los componentes que únicamente consultan. */
public interface LecturaDAO<T, ID> {
    Optional<T> buscarPorId(ID id);

    List<T> listarTodos();
}

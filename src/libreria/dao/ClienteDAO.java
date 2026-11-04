/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ClienteDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import java.util.Optional;
import libreria.model.Cliente;

public interface ClienteDAO extends LecturaDAO<Cliente, Integer>, EscrituraDAO<Cliente, Integer> {
    /** SQL futuro: SELECT ... FROM cliente WHERE documento = ? */
    Optional<Cliente> buscarPorDocumento(String documento);
}

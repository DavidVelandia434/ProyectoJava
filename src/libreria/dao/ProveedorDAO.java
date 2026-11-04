/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ProveedorDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import libreria.model.Proveedor;

public interface ProveedorDAO extends LecturaDAO<Proveedor, Integer>, EscrituraDAO<Proveedor, Integer> {
}

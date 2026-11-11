/*
 * Programa : Sistema de gestión de librería
 * Archivo  : CategoriaDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import libreria.model.Categoria;

public interface CategoriaDAO extends LecturaDAO<Categoria, Integer>, EscrituraDAO<Categoria, Integer> {
}

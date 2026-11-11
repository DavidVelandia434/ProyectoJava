/*
 * Programa : Sistema de gestión de librería
 * Archivo  : AutorDAO.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao;

import libreria.model.Autor;

public interface AutorDAO extends LecturaDAO<Autor, Integer>, EscrituraDAO<Autor, Integer> {
}

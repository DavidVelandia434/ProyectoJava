/*
 * Programa : Sistema de gestión de librería
 * Archivo  : AutorDAOMemoria.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.memoria;

import libreria.dao.AutorDAO;
import libreria.database.BaseDatosEnMemoria;
import libreria.model.Autor;

public class AutorDAOMemoria extends DAOMemoriaBase<Autor> implements AutorDAO {
    public AutorDAOMemoria(BaseDatosEnMemoria bd) { super(bd.autores); }
    @Override protected Integer obtenerId(Autor a) { return a.getId(); }
    @Override protected void asignarId(Autor a, int id) { a.setId(id); }
}

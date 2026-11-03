/*
 * Programa : Sistema de gestión de librería
 * Archivo  : CategoriaDAOMemoria.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.memoria;

import libreria.dao.CategoriaDAO;
import libreria.database.BaseDatosEnMemoria;
import libreria.model.Categoria;

public class CategoriaDAOMemoria extends DAOMemoriaBase<Categoria> implements CategoriaDAO {
    public CategoriaDAOMemoria(BaseDatosEnMemoria bd) { super(bd.categorias); }
    @Override protected Integer obtenerId(Categoria c) { return c.getId(); }
    @Override protected void asignarId(Categoria c, int id) { c.setId(id); }
}

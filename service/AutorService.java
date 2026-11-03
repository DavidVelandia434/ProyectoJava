/*
 * Programa : Sistema de gestión de librería
 * Archivo  : AutorService.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.service;

import java.util.List;
import libreria.dao.AutorDAO;
import libreria.dao.LibroDAO;
import libreria.model.Autor;

public class AutorService {
    private final AutorDAO autorDAO;
    private final LibroDAO libroDAO;

    public AutorService(AutorDAO autorDAO, LibroDAO libroDAO) {
        this.autorDAO = autorDAO;
        this.libroDAO = libroDAO;
    }

    public Autor registrar(String nombre, String nacionalidad) {
        Autor autor = new Autor(null, Validador.textoObligatorio(nombre, "nombre"),
                Validador.textoObligatorio(nacionalidad, "nacionalidad"));
        return autorDAO.insertar(autor);
    }

    public List<Autor> listar() {
        return autorDAO.listarTodos();
    }

    public ResultadoEliminacion eliminar(int id) {
        if (autorDAO.buscarPorId(id).isEmpty()) return ResultadoEliminacion.NO_ENCONTRADO;
        boolean enUso = libroDAO.listarTodos().stream().anyMatch(l -> l.getIdsAutores().contains(id));
        if (enUso) return ResultadoEliminacion.RECHAZADO_POR_RESTRICCION;
        autorDAO.eliminar(id);
        return ResultadoEliminacion.ELIMINADO;
    }
}

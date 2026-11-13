package libreria.service;

import java.util.List;
import libreria.dao.CategoriaDAO;
import libreria.dao.LibroDAO;
import libreria.model.Categoria;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO;
    private final LibroDAO libroDAO;

    public CategoriaService(CategoriaDAO categoriaDAO, LibroDAO libroDAO) {
        this.categoriaDAO = categoriaDAO;
        this.libroDAO = libroDAO;
    }

    public Categoria registrar(String nombre) {
        String n = Validador.textoObligatorio(nombre, "nombre");
        boolean repetida = categoriaDAO.listarTodos().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(n));
        if (repetida) throw new ReglaNegocioException("Ya existe una categoría con ese nombre.");
        return categoriaDAO.insertar(new Categoria(null, n));
    }

    public List<Categoria> listar() {
        return categoriaDAO.listarTodos();
    }

    public ResultadoEliminacion eliminar(int id) {
        if (categoriaDAO.buscarPorId(id).isEmpty()) return ResultadoEliminacion.NO_ENCONTRADO;
        if (!libroDAO.listarPorCategoria(id).isEmpty()) return ResultadoEliminacion.RECHAZADO_POR_RESTRICCION;
        categoriaDAO.eliminar(id);
        return ResultadoEliminacion.ELIMINADO;
    }
}

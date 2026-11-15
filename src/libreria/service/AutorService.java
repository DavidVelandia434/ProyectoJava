package libreria.service;

import java.util.List;
import java.util.Optional;
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

    public Optional<Autor> buscarPorId(int id) {
        return autorDAO.buscarPorId(id);
    }

    public void actualizarDatos(int id, String nombre, String nacionalidad) {
        Autor actual = autorDAO.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("El autor con ID " + id + " no existe."));
        String nombreFinal = (nombre == null || nombre.trim().isEmpty())
                ? actual.getNombre()
                : Validador.textoObligatorio(nombre, "nombre");
        String nacFinal = (nacionalidad == null || nacionalidad.trim().isEmpty())
                ? actual.getNacionalidad()
                : Validador.textoObligatorio(nacionalidad, "nacionalidad");
        autorDAO.actualizar(new Autor(id, nombreFinal, nacFinal));
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

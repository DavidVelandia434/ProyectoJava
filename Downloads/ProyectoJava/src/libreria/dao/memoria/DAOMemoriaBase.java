package libreria.dao.memoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libreria.dao.EscrituraDAO;
import libreria.dao.LecturaDAO;
import libreria.database.Almacen;

abstract class DAOMemoriaBase<T> implements LecturaDAO<T, Integer>, EscrituraDAO<T, Integer> {
    protected final Almacen<T> almacen;

    protected DAOMemoriaBase(Almacen<T> almacen) {
        this.almacen = almacen;
    }

    protected abstract Integer obtenerId(T entidad);
    protected abstract void asignarId(T entidad, int id);

    @Override
    public Optional<T> buscarPorId(Integer id) {
        return Optional.ofNullable(almacen.filas().get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(almacen.filas().values());
    }

    @Override
    public T insertar(T entidad) {
        int id = almacen.siguienteId();
        asignarId(entidad, id);
        almacen.filas().put(id, entidad);
        return entidad;
    }

    @Override
    public boolean actualizar(T entidad) {
        Integer id = obtenerId(entidad);
        if (id == null || !almacen.filas().containsKey(id)) {
            return false;
        }
        almacen.filas().put(id, entidad);
        return true;
    }

    @Override
    public boolean eliminar(Integer id) {
        return almacen.filas().remove(id) != null;
    }
}

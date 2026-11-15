package libreria.dao;

import java.util.List;
import java.util.Optional;

public interface LecturaDAO<T, ID> {
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}

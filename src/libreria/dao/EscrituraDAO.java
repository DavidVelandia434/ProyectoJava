package libreria.dao;

public interface EscrituraDAO<T, ID> {
    T insertar(T entidad);
    boolean actualizar(T entidad);
    boolean eliminar(ID id);
}

package libreria.dao.memoria;

import libreria.dao.ProveedorDAO;
import libreria.database.BaseDatosEnMemoria;
import libreria.model.Proveedor;

public class ProveedorDAOMemoria extends DAOMemoriaBase<Proveedor> implements ProveedorDAO {
    public ProveedorDAOMemoria(BaseDatosEnMemoria bd) { super(bd.proveedores); }
    @Override protected Integer obtenerId(Proveedor p) { return p.getId(); }
    @Override protected void asignarId(Proveedor p, int id) { p.setId(id); }
}

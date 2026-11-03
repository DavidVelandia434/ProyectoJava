/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ClienteDAOMemoria.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.dao.memoria;

import java.util.Optional;
import libreria.dao.ClienteDAO;
import libreria.database.BaseDatosEnMemoria;
import libreria.model.Cliente;

public class ClienteDAOMemoria extends DAOMemoriaBase<Cliente> implements ClienteDAO {
    public ClienteDAOMemoria(BaseDatosEnMemoria bd) { super(bd.clientes); }
    @Override protected Integer obtenerId(Cliente c) { return c.getId(); }
    @Override protected void asignarId(Cliente c, int id) { c.setId(id); }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        return almacen.filas().values().stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst();
    }
}

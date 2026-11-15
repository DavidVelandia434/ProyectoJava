package libreria.dao;

import java.util.Optional;
import libreria.model.Cliente;

public interface ClienteDAO extends LecturaDAO<Cliente, Integer>, EscrituraDAO<Cliente, Integer> {
    Optional<Cliente> buscarPorDocumento(String documento);
}

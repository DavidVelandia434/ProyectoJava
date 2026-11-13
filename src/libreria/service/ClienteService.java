package libreria.service;

import java.util.List;
import libreria.dao.ClienteDAO;
import libreria.dao.VentaRepository;
import libreria.model.Cliente;

public class ClienteService {
    private final ClienteDAO clienteDAO;
    private final VentaRepository ventaRepository;

    public ClienteService(ClienteDAO clienteDAO, VentaRepository ventaRepository) {
        this.clienteDAO = clienteDAO;
        this.ventaRepository = ventaRepository;
    }

    public Cliente registrar(String nombre, String documento, String telefono, String correo) {
        String doc = Validador.textoObligatorio(documento, "documento");
        if (clienteDAO.buscarPorDocumento(doc).isPresent()) {
            throw new ReglaNegocioException("Ya existe un cliente con ese documento.");
        }
        Cliente c = new Cliente(null, Validador.textoObligatorio(nombre, "nombre"), doc,
                Validador.textoObligatorio(telefono, "teléfono"), Validador.correoValido(correo));
        return clienteDAO.insertar(c);
    }

    public void actualizarContacto(int id, String nombre, String telefono, String correo) {
        Cliente c = clienteDAO.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("El cliente no existe."));
        Cliente actualizado = new Cliente(c.getId(),
                Validador.textoObligatorio(nombre, "nombre"), c.getDocumento(),
                Validador.textoObligatorio(telefono, "teléfono"), Validador.correoValido(correo));
        clienteDAO.actualizar(actualizado);
    }

    public List<Cliente> listar() {
        return clienteDAO.listarTodos();
    }

    public ResultadoEliminacion eliminar(int id) {
        if (clienteDAO.buscarPorId(id).isEmpty()) return ResultadoEliminacion.NO_ENCONTRADO;
        if (ventaRepository.existeVentaConCliente(id)) return ResultadoEliminacion.RECHAZADO_POR_RESTRICCION;
        clienteDAO.eliminar(id);
        return ResultadoEliminacion.ELIMINADO;
    }
}

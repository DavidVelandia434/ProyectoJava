package libreria.service;

import java.util.List;
import java.util.Optional;
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

    public Optional<Cliente> buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    public void actualizarContacto(int id, String nombre, String telefono, String correo) {
        actualizarCliente(id, nombre, null, telefono, correo);
    }

    public void actualizarCliente(int id, String nombre, String documento, String telefono, String correo) {
        Cliente c = clienteDAO.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("El cliente con ID " + id + " no existe."));

        String nombreFinal = (nombre == null || nombre.trim().isEmpty())
                ? c.getNombre()
                : Validador.textoObligatorio(nombre, "nombre");
        String docFinal = (documento == null || documento.trim().isEmpty())
                ? c.getDocumento()
                : Validador.textoObligatorio(documento, "documento");
        String telFinal = (telefono == null || telefono.trim().isEmpty())
                ? c.getTelefono()
                : Validador.textoObligatorio(telefono, "teléfono");
        String correoFinal = (correo == null || correo.trim().isEmpty())
                ? c.getCorreo()
                : Validador.correoValido(correo);

        Optional<Cliente> existenteDoc = clienteDAO.buscarPorDocumento(docFinal);
        if (existenteDoc.isPresent() && !existenteDoc.get().getId().equals(id)) {
            throw new ReglaNegocioException("Ya existe otro cliente registrado con el documento " + docFinal + ".");
        }

        Cliente actualizado = new Cliente(c.getId(), nombreFinal, docFinal, telFinal, correoFinal);
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

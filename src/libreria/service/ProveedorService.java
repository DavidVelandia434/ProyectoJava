package libreria.service;

import java.util.List;
import libreria.dao.LibroDAO;
import libreria.dao.ProveedorDAO;
import libreria.model.Proveedor;

public class ProveedorService {
    private final ProveedorDAO proveedorDAO;
    private final LibroDAO libroDAO;

    public ProveedorService(ProveedorDAO proveedorDAO, LibroDAO libroDAO) {
        this.proveedorDAO = proveedorDAO;
        this.libroDAO = libroDAO;
    }

    public Proveedor registrar(String nombre, String telefono, String correo) {
        Proveedor p = new Proveedor(null,
                Validador.textoObligatorio(nombre, "nombre"),
                Validador.textoObligatorio(telefono, "teléfono"),
                Validador.correoValido(correo));
        return proveedorDAO.insertar(p);
    }

    public void actualizarDatos(int id, String nombre, String telefono, String correo) {
        Proveedor p = proveedorDAO.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("El proveedor no existe."));
        Proveedor actualizado = new Proveedor(p.getId(),
                Validador.textoObligatorio(nombre, "nombre"),
                Validador.textoObligatorio(telefono, "teléfono"),
                Validador.correoValido(correo));
        proveedorDAO.actualizar(actualizado);
    }

    public List<Proveedor> listar() {
        return proveedorDAO.listarTodos();
    }

    public ResultadoEliminacion eliminar(int id) {
        if (proveedorDAO.buscarPorId(id).isEmpty()) return ResultadoEliminacion.NO_ENCONTRADO;
        boolean enUso = libroDAO.listarTodos().stream()
                .anyMatch(l -> l.getIdProveedor() != null && l.getIdProveedor() == id);
        if (enUso) return ResultadoEliminacion.RECHAZADO_POR_RESTRICCION;
        proveedorDAO.eliminar(id);
        return ResultadoEliminacion.ELIMINADO;
    }
}

package libreria.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import libreria.dao.AutorDAO;
import libreria.dao.CategoriaDAO;
import libreria.dao.LibroDAO;
import libreria.dao.ProveedorDAO;
import libreria.dao.VentaRepository;
import libreria.model.Libro;

public class LibroService {
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProveedorDAO proveedorDAO;
    private final AutorDAO autorDAO;
    private final VentaRepository ventaRepository;

    public LibroService(LibroDAO libroDAO, CategoriaDAO categoriaDAO,
                        ProveedorDAO proveedorDAO, AutorDAO autorDAO,
                        VentaRepository ventaRepository) {
        this.libroDAO = libroDAO;
        this.categoriaDAO = categoriaDAO;
        this.proveedorDAO = proveedorDAO;
        this.autorDAO = autorDAO;
        this.ventaRepository = ventaRepository;
    }

    public Libro registrar(String isbn, String titulo, BigDecimal precio, int stock,
                            int idCategoria, int idProveedor, List<Integer> idsAutores) {
        String isbnLimpio = Validador.textoObligatorio(isbn, "ISBN");
        String tituloLimpio = Validador.textoObligatorio(titulo, "título");
        Validador.precioPositivo(precio);
        Validador.noNegativo(stock, "stock");

        if (libroDAO.buscarPorIsbn(isbnLimpio).isPresent()) {
            throw new ReglaNegocioException("Ya existe un libro con el ISBN " + isbnLimpio);
        }
        if (categoriaDAO.buscarPorId(idCategoria).isEmpty()) {
            throw new ReglaNegocioException("La categoría indicada no existe.");
        }
        if (proveedorDAO.buscarPorId(idProveedor).isEmpty()) {
            throw new ReglaNegocioException("El proveedor indicado no existe.");
        }
        if (idsAutores == null || idsAutores.isEmpty()) {
            throw new ReglaNegocioException("El libro debe tener al menos un autor.");
        }
        for (Integer idAutor : idsAutores) {
            if (autorDAO.buscarPorId(idAutor).isEmpty()) {
                throw new ReglaNegocioException("El autor con id " + idAutor + " no existe.");
            }
        }
        return libroDAO.insertar(new Libro(null, isbnLimpio, tituloLimpio, precio, stock,
                idCategoria, idProveedor, idsAutores));
    }

    public Optional<Libro> buscarPorId(int id) { return libroDAO.buscarPorId(id); }
    public List<Libro> listarPorTitulo() { return libroDAO.listarPorTitulo(); }
    public List<Libro> listarPorCategoria(int idCategoria) { return libroDAO.listarPorCategoria(idCategoria); }

    public List<Libro> listarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new ReglaNegocioException("El precio mínimo no puede ser mayor que el máximo.");
        }
        return libroDAO.listarPorRangoPrecio(min, max);
    }

    public List<Libro> listarStockBajo(int umbral) { return libroDAO.listarConStockInferiorA(umbral); }

    public void actualizarPrecio(int idLibro, BigDecimal nuevoPrecio) {
        Validador.precioPositivo(nuevoPrecio);
        existeOError(idLibro);
        libroDAO.actualizarPrecio(idLibro, nuevoPrecio);
    }

    public void corregirTitulo(int idLibro, String nuevoTitulo) {
        String t = Validador.textoObligatorio(nuevoTitulo, "título");
        existeOError(idLibro);
        libroDAO.actualizarTitulo(idLibro, t);
    }

    public void ajustarStock(int idLibro, int nuevoStock, String motivo) {
        Validador.noNegativo(nuevoStock, "existencias");
        Validador.textoObligatorio(motivo, "motivo del ajuste");
        existeOError(idLibro);
        libroDAO.actualizarStock(idLibro, nuevoStock);
    }

    public ResultadoEliminacion eliminar(int idLibro) {
        if (libroDAO.buscarPorId(idLibro).isEmpty()) return ResultadoEliminacion.NO_ENCONTRADO;
        if (ventaRepository.existeVentaConLibro(idLibro)) return ResultadoEliminacion.RECHAZADO_POR_RESTRICCION;
        libroDAO.eliminar(idLibro);
        return ResultadoEliminacion.ELIMINADO;
    }

    private void existeOError(int idLibro) {
        if (libroDAO.buscarPorId(idLibro).isEmpty()) {
            throw new ReglaNegocioException("El libro con id " + idLibro + " no existe.");
        }
    }
}

/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibreriaFacade.java
 * Paquete  : libreria.gui (Carpeta independiente para la Fachada y la Interfaz Gráfica AWT)
 * Fecha    : 06/11/2026
 */
package libreria.gui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import libreria.dao.AutorDAO;
import libreria.dao.CategoriaDAO;
import libreria.dao.ClienteDAO;
import libreria.dao.LibroDAO;
import libreria.dao.PersistenciaException;
import libreria.dao.ProveedorDAO;
import libreria.dao.VentaRepository;
import libreria.dao.jdbc.AutorDAOJdbc;
import libreria.dao.jdbc.CategoriaDAOJdbc;
import libreria.dao.jdbc.ClienteDAOJdbc;
import libreria.dao.jdbc.LibroDAOJdbc;
import libreria.dao.jdbc.ProveedorDAOJdbc;
import libreria.dao.jdbc.VentaRepositoryJdbc;
import libreria.dao.memoria.AutorDAOMemoria;
import libreria.dao.memoria.CategoriaDAOMemoria;
import libreria.dao.memoria.ClienteDAOMemoria;
import libreria.dao.memoria.LibroDAOMemoria;
import libreria.dao.memoria.ProveedorDAOMemoria;
import libreria.dao.memoria.VentaRepositoryMemoria;
import libreria.database.BaseDatosEnMemoria;
import libreria.database.ConexionBD;
import libreria.database.DatosPrueba;
import libreria.model.Autor;
import libreria.model.Categoria;
import libreria.model.Cliente;
import libreria.model.DetalleVenta;
import libreria.model.Libro;
import libreria.model.Proveedor;
import libreria.model.Venta;
import libreria.service.AutorService;
import libreria.service.CategoriaService;
import libreria.service.ClienteService;
import libreria.service.DescuentoPorVolumen;
import libreria.service.LibroService;
import libreria.service.PoliticaDescuento;
import libreria.service.ProveedorService;
import libreria.service.ReglaNegocioException;
import libreria.service.ReporteService;
import libreria.service.ResultadoEliminacion;
import libreria.service.VentaService;

/**
 * PATRÓN DE DISEÑO FACADE (FACHADA)
 * =========================================================================
 * Centraliza y unifica el acceso a todos los subsistemas de lógica de negocio
 * (LibroService, AutorService, CategoriaService, ProveedorService, ClienteService,
 * VentaService, ReporteService) y a la capa de configuración de persistencia
 * (ConexionBD / BaseDatosEnMemoria).
 *
 * La interfaz gráfica AWT (VentanaPrincipalAWT) interactúa ÚNICAMENTE con esta
 * fachada y NO ejecuta ninguna validación, cálculo ni proceso de negocio por sí misma.
 */
public class LibreriaFacade {

    private final ConexionBD conexionBD;
    private boolean modoJdbcActivo;

    private AutorService autorService;
    private CategoriaService categoriaService;
    private ProveedorService proveedorService;
    private ClienteService clienteService;
    private LibroService libroService;
    private VentaService ventaService;
    private ReporteService reporteService;

    // Carrito temporal gestionado del lado de la fachada para preparar la venta
    private final Map<Integer, Integer> carritoTemporal = new LinkedHashMap<>();

    public LibreriaFacade() {
        this.conexionBD = new ConexionBD();
        // Intenta iniciar en modo JDBC (Supabase PostgreSQL); si no está disponible o sin tablas,
        // hace fallback automático a BaseDatosEnMemoria con DatosPrueba para correr sin fricciones en VS Code.
        if (conexionBD.probarConexionYEsquema()) {
            inicializarSubsistema(true);
        } else {
            inicializarSubsistema(false);
        }
    }

    /** Ensambla los DAOs y Servicios según el modo de persistencia seleccionado. */
    public final void inicializarSubsistema(boolean usarJdbc) {
        AutorDAO autorDAO;
        CategoriaDAO categoriaDAO;
        ProveedorDAO proveedorDAO;
        ClienteDAO clienteDAO;
        LibroDAO libroDAO;
        VentaRepository ventaRepository;

        if (usarJdbc && conexionBD.probarConexionYEsquema()) {
            this.modoJdbcActivo = true;
            autorDAO = new AutorDAOJdbc(conexionBD);
            categoriaDAO = new CategoriaDAOJdbc(conexionBD);
            proveedorDAO = new ProveedorDAOJdbc(conexionBD);
            clienteDAO = new ClienteDAOJdbc(conexionBD);
            libroDAO = new LibroDAOJdbc(conexionBD);
            ventaRepository = new VentaRepositoryJdbc(conexionBD);
        } else {
            this.modoJdbcActivo = false;
            BaseDatosEnMemoria bd = new BaseDatosEnMemoria();
            autorDAO = new AutorDAOMemoria(bd);
            categoriaDAO = new CategoriaDAOMemoria(bd);
            proveedorDAO = new ProveedorDAOMemoria(bd);
            clienteDAO = new ClienteDAOMemoria(bd);
            libroDAO = new LibroDAOMemoria(bd);
            ventaRepository = new VentaRepositoryMemoria(bd, libroDAO);
            DatosPrueba.cargar(autorDAO, categoriaDAO, proveedorDAO, clienteDAO, libroDAO, ventaRepository);
        }

        // Patrón Strategy para la política de descuento por volumen
        PoliticaDescuento politica = new DescuentoPorVolumen(5, new BigDecimal("0.10"));
        this.libroService = new LibroService(libroDAO, categoriaDAO, proveedorDAO, autorDAO, ventaRepository);
        this.autorService = new AutorService(autorDAO, libroDAO);
        this.categoriaService = new CategoriaService(categoriaDAO, libroDAO);
        this.proveedorService = new ProveedorService(proveedorDAO, libroDAO);
        this.clienteService = new ClienteService(clienteDAO, ventaRepository);
        this.ventaService = new VentaService(ventaRepository, libroDAO, clienteDAO, politica);
        this.reporteService = new ReporteService(libroDAO, categoriaDAO, clienteDAO, ventaRepository);
    }

    public String obtenerEstadoModoPersistencia() {
        return modoJdbcActivo
                ? "CONECTADO: PostgreSQL (Supabase)"
                : "MODO LOCAL: Memoria";
    }

    public boolean isModoJdbcActivo() {
        return modoJdbcActivo;
    }

    public String probarConexionSupabase() {
        return conexionBD.diagnosticarConexion();
    }

    public String cambiarModoPersistencia(boolean intentarJdbc) {
        if (intentarJdbc) {
            if (conexionBD.probarConexionYEsquema()) {
                inicializarSubsistema(true);
                return "Modo cambiado exitosamente a PostgreSQL (Supabase).";
            } else {
                inicializarSubsistema(false);
                return "No fue posible conectar con Supabase. Se mantiene el modo en memoria local.";
            }
        } else {
            inicializarSubsistema(false);
            return "Modo cambiado a almacenamiento en memoria local.";
        }
    }

    // =========================================================================
    // 1. FACHADA DE LIBROS (Consultas 1 a 4 + CRUD + UPDATE 1, 2 y 3)
    // =========================================================================

    /** Consulta 1: Listado de libros ordenados alfabéticamente por título. */
    public List<String> obtenerLibrosFormateados() {
        return formatearListaLibros(libroService.listarPorTitulo());
    }

    public List<String> obtenerLibrosCompactosParaChoice() {
        List<String> res = new ArrayList<>();
        for (Libro l : libroService.listarPorTitulo()) {
            String t = l.getTitulo() != null ? l.getTitulo() : "";
            if (t.length() > 22) {
                t = t.substring(0, 20) + "..";
            }
            res.add("[" + l.getId() + "] " + t + " ($" + l.getPrecio() + ")");
        }
        return res;
    }

    /**
     * Devuelve los datos de un libro como arreglo de Strings para poblar el formulario:
     * [0]=id, [1]=isbn, [2]=titulo, [3]=precio, [4]=stock, [5]=idCategoria, [6]=idProveedor, [7]=idsAutores (csv)
     */
    public String[] obtenerDatosLibroPorId(String textoIdLibro) {
        int id = parsearEntero(textoIdLibro, "ID del Libro");
        Optional<Libro> opt = libroService.buscarPorId(id);
        if (opt.isEmpty()) {
            return null;
        }
        Libro l = opt.get();
        String autoresCsv = l.getIdsAutores().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return new String[] {
            String.valueOf(l.getId()),
            l.getIsbn() != null ? l.getIsbn() : "",
            l.getTitulo() != null ? l.getTitulo() : "",
            l.getPrecio() != null ? l.getPrecio().toPlainString() : "",
            String.valueOf(l.getStock()),
            String.valueOf(l.getIdCategoria()),
            String.valueOf(l.getIdProveedor()),
            autoresCsv.isEmpty() ? "1" : autoresCsv
        };
    }

    /** Consulta 2: Filtrar libros por categoría seleccionada. */
    public List<String> filtrarLibrosPorCategoria(String textoIdCategoria) {
        int idCat = extraerIdDeItem(textoIdCategoria, "ID de Categoría");
        return formatearListaLibros(libroService.listarPorCategoria(idCat));
    }

    /** Consulta 3: Filtrar libros por rango de precios. */
    public List<String> filtrarLibrosPorRangoPrecio(String textoMin, String textoMax) {
        BigDecimal min = parsearDecimal(textoMin, "Precio mínimo");
        BigDecimal max = parsearDecimal(textoMax, "Precio máximo");
        return formatearListaLibros(libroService.listarPorRangoPrecio(min, max));
    }

    /** Consulta 4 (RF08): Filtrar libros con stock inferior al umbral. */
    public List<String> filtrarLibrosStockBajo(String textoUmbral) {
        int umbral = parsearEntero(textoUmbral, "Umbral de stock");
        return formatearListaLibros(libroService.listarStockBajo(umbral));
    }

    public String registrarLibro(String isbn, String titulo, String textoPrecio, String textoStock,
                                 String itemCategoria, String itemProveedor, String textoIdsAutores) {
        BigDecimal precio = parsearDecimal(textoPrecio, "Precio");
        int stock = parsearEntero(textoStock, "Stock inicial");
        int idCategoria = extraerIdDeItem(itemCategoria, "Categoría");
        int idProveedor = extraerIdDeItem(itemProveedor, "Proveedor");
        List<Integer> idsAutores = parsearListaIds(textoIdsAutores);

        Libro creado = libroService.registrar(isbn, titulo, precio, stock, idCategoria, idProveedor, idsAutores);
        return "Libro registrado exitosamente: " + creado.toString();
    }

    /** Actualización integral de un libro (cubre UPDATE 1 precio, UPDATE 2 título, UPDATE 3 stock y relaciones). */
    public String actualizarLibroCompleto(String textoIdLibro, String isbn, String titulo,
                                          String textoPrecio, String textoStock,
                                          String itemCategoria, String itemProveedor, String textoIdsAutores) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro (seleccione un libro de la lista)");
        BigDecimal precio = parsearDecimal(textoPrecio, "Precio");
        int stock = parsearEntero(textoStock, "Stock");
        int idCategoria = extraerIdDeItem(itemCategoria, "Categoría");
        int idProveedor = extraerIdDeItem(itemProveedor, "Proveedor");
        List<Integer> idsAutores = parsearListaIds(textoIdsAutores);

        Libro actualizado = libroService.actualizarLibroCompleto(
                idLibro, isbn, titulo, precio, stock, idCategoria, idProveedor, idsAutores);
        return "Libro #" + actualizado.getId() + " actualizado correctamente: " + actualizado.getTitulo();
    }

    /** UPDATE 1: Modificación del precio de un libro. */
    public String actualizarPrecioLibro(String textoIdLibro, String textoNuevoPrecio) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        BigDecimal nuevoPrecio = parsearDecimal(textoNuevoPrecio, "Nuevo precio");
        libroService.actualizarPrecio(idLibro, nuevoPrecio);
        return "Precio actualizado a $" + nuevoPrecio + " para el libro #" + idLibro + ".";
    }

    /** UPDATE 2: Corrección del título de un libro. */
    public String corregirTituloLibro(String textoIdLibro, String nuevoTitulo) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        libroService.corregirTitulo(idLibro, nuevoTitulo);
        return "Título actualizado para el libro #" + idLibro + ".";
    }

    /** UPDATE 3: Ajuste autorizado de existencias con motivo. */
    public String ajustarStockLibro(String textoIdLibro, String textoNuevoStock, String motivo) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        int nuevoStock = parsearEntero(textoNuevoStock, "Nuevo stock");
        libroService.ajustarStock(idLibro, nuevoStock, motivo);
        return "Stock del libro #" + idLibro + " actualizado a " + nuevoStock + ".";
    }

    public String eliminarLibro(String textoIdLibro) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        ResultadoEliminacion res = libroService.eliminar(idLibro);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return "Libro #" + idLibro + " eliminado correctamente.";
    }

    // =========================================================================
    // 2. FACHADA DE VENTAS Y CARRITO (RF03 a RF07 + Transacción)
    // =========================================================================

    public String obtenerDescripcionPoliticaDescuento() {
        return ventaService.descripcionPolitica();
    }

    public String agregarItemAlCarrito(String itemLibro, String textoCantidad) {
        int idLibro = extraerIdDeItem(itemLibro, "Libro");
        int cantidad = parsearEntero(textoCantidad, "Cantidad");
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor que cero.");
        }
        Libro libro = libroService.buscarPorId(idLibro)
                .orElseThrow(() -> new ReglaNegocioException("El libro seleccionado no existe."));
        int acumulado = carritoTemporal.getOrDefault(idLibro, 0) + cantidad;
        if (acumulado > libro.getStock()) {
            throw new ReglaNegocioException("Stock insuficiente para \"" + libro.getTitulo()
                    + "\". Disponible: " + libro.getStock() + ", en carrito: " + acumulado);
        }
        carritoTemporal.put(idLibro, acumulado);
        return "Agregado al carrito: " + cantidad + " x " + libro.getTitulo();
    }

    public void limpiarCarrito() {
        carritoTemporal.clear();
    }

    public List<String> obtenerLineasCarritoFormateadas() {
        List<String> lineas = new ArrayList<>();
        BigDecimal subtotalAcumulado = BigDecimal.ZERO;
        int totalUnidades = 0;

        for (Map.Entry<Integer, Integer> entry : carritoTemporal.entrySet()) {
            Optional<Libro> opt = libroService.buscarPorId(entry.getKey());
            if (opt.isPresent()) {
                Libro l = opt.get();
                int cant = entry.getValue();
                BigDecimal sub = l.getPrecio().multiply(BigDecimal.valueOf(cant));
                subtotalAcumulado = subtotalAcumulado.add(sub);
                totalUnidades += cant;
                lineas.add("Libro [" + l.getId() + "] " + l.getTitulo()
                        + " | Cant: " + cant + " | Unit: $" + l.getPrecio() + " | Subtotal: $" + sub);
            }
        }
        if (!lineas.isEmpty()) {
            lineas.add("----------------------------------------------------------------------");
            lineas.add("Unidades en carrito: " + totalUnidades + " | Subtotal estimado (antes de descuento): $" + subtotalAcumulado);
        }
        return lineas;
    }

    public String confirmarVentaCarrito(String itemCliente) {
        int idCliente = extraerIdDeItem(itemCliente, "Cliente");
        if (carritoTemporal.isEmpty()) {
            throw new ReglaNegocioException("El carrito está vacío. Agregue al menos un libro antes de confirmar.");
        }
        Map<Integer, Integer> copiaPedido = new LinkedHashMap<>(carritoTemporal);
        Venta registrada = ventaService.registrarVenta(idCliente, copiaPedido);
        carritoTemporal.clear();
        return "Venta #" + registrada.getId() + " registrada exitosamente | Subtotal: $"
                + registrada.getSubtotal() + " | Descuento: $" + registrada.getDescuento()
                + " | TOTAL: $" + registrada.getTotal();
    }

    /** Consultas 11 y 12: Listado de ventas con sus detalles e información de clientes y libros. */
    public List<String> obtenerTodasLasVentasFormateadas() {
        return formatearListaVentas(ventaService.listarTodas());
    }

    public List<String> obtenerVentasPorCliente(String itemCliente) {
        int idCliente = extraerIdDeItem(itemCliente, "Cliente");
        return formatearListaVentas(ventaService.historialPorCliente(idCliente));
    }

    /** RF07: Historial de ventas filtrado por rango de fechas. */
    public List<String> obtenerVentasPorFechas(String textoDesde, String textoHasta) {
        LocalDate desde = parsearFecha(textoDesde, "Fecha inicial (YYYY-MM-DD)");
        LocalDate hasta = parsearFecha(textoHasta, "Fecha final (YYYY-MM-DD)");
        return formatearListaVentas(ventaService.historialPorFechas(desde, hasta));
    }

    // =========================================================================
    // 3. FACHADA DE CLIENTES (CRUD + UPDATE 4)
    // =========================================================================

    public List<String> obtenerClientesFormateados() {
        List<String> res = new ArrayList<>();
        for (Cliente c : clienteService.listar()) {
            res.add(c.toString());
        }
        return res;
    }

    public List<String> obtenerClientesCompactosParaChoice() {
        List<String> res = new ArrayList<>();
        for (Cliente c : clienteService.listar()) {
            String n = c.getNombre() != null ? c.getNombre() : "";
            if (n.length() > 18) {
                n = n.substring(0, 16) + "..";
            }
            res.add("[" + c.getId() + "] " + n);
        }
        return res;
    }

    /**
     * Devuelve los datos de un cliente por ID:
     * [0]=id, [1]=nombre, [2]=documento, [3]=telefono, [4]=correo
     */
    public String[] obtenerDatosClientePorId(String textoIdCliente) {
        int id = parsearEntero(textoIdCliente, "ID del Cliente");
        Optional<Cliente> opt = clienteService.buscarPorId(id);
        if (opt.isEmpty()) {
            return null;
        }
        Cliente c = opt.get();
        return new String[] {
            String.valueOf(c.getId()),
            c.getNombre() != null ? c.getNombre() : "",
            c.getDocumento() != null ? c.getDocumento() : "",
            c.getTelefono() != null ? c.getTelefono() : "",
            c.getCorreo() != null ? c.getCorreo() : ""
        };
    }

    public String registrarCliente(String nombre, String documento, String telefono, String correo) {
        Cliente c = clienteService.registrar(nombre, documento, telefono, correo);
        return "Cliente registrado correctamente: " + c.toString();
    }

    /** UPDATE 4: Modificación de datos de contacto de un cliente. */
    public String actualizarContactoCliente(String textoIdCliente, String nombre, String telefono, String correo) {
        int idCliente = parsearEntero(textoIdCliente, "ID del Cliente");
        clienteService.actualizarContacto(idCliente, nombre, telefono, correo);
        return "Datos actualizados correctamente para el cliente #" + idCliente + ".";
    }

    /** UPDATE 4 (extendido): Actualización de todos los datos de un cliente. */
    public String actualizarClienteCompleto(String textoIdCliente, String nombre, String documento, String telefono, String correo) {
        int idCliente = parsearEntero(textoIdCliente, "ID del Cliente");
        clienteService.actualizarCliente(idCliente, nombre, documento, telefono, correo);
        return "Datos del cliente #" + idCliente + " actualizados correctamente.";
    }

    public String eliminarCliente(String textoIdCliente) {
        int idCliente = parsearEntero(textoIdCliente, "ID del Cliente");
        ResultadoEliminacion res = clienteService.eliminar(idCliente);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return "Cliente #" + idCliente + " eliminado correctamente.";
    }

    // =========================================================================
    // 4. FACHADA DE CATÁLOGO: AUTORES, CATEGORÍAS Y PROVEEDORES (CRUD + UPDATE 5)
    // =========================================================================

    public List<String> obtenerAutoresFormateados() {
        List<String> res = new ArrayList<>();
        for (Autor a : autorService.listar()) {
            res.add(a.toString());
        }
        return res;
    }

    public String[] obtenerDatosAutorPorId(String textoIdAutor) {
        int id = parsearEntero(textoIdAutor, "ID del Autor");
        Optional<Autor> opt = autorService.buscarPorId(id);
        if (opt.isEmpty()) return null;
        Autor a = opt.get();
        return new String[] { String.valueOf(a.getId()), a.getNombre(), a.getNacionalidad() };
    }

    public String registrarAutor(String nombre, String nacionalidad) {
        Autor a = autorService.registrar(nombre, nacionalidad);
        return "Autor registrado correctamente: " + a.toString();
    }

    public String actualizarAutor(String textoIdAutor, String nombre, String nacionalidad) {
        int id = parsearEntero(textoIdAutor, "ID del Autor");
        autorService.actualizarDatos(id, nombre, nacionalidad);
        return "Autor #" + id + " actualizado correctamente.";
    }

    public String eliminarAutor(String textoIdAutor) {
        int id = parsearEntero(textoIdAutor, "ID del Autor");
        ResultadoEliminacion res = autorService.eliminar(id);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return res.getMensaje();
    }

    public List<String> obtenerCategoriasFormateadas() {
        List<String> res = new ArrayList<>();
        for (Categoria c : categoriaService.listar()) {
            res.add(c.toString());
        }
        return res;
    }

    public String[] obtenerDatosCategoriaPorId(String textoIdCategoria) {
        int id = parsearEntero(textoIdCategoria, "ID de la Categoría");
        Optional<Categoria> opt = categoriaService.buscarPorId(id);
        if (opt.isEmpty()) return null;
        Categoria c = opt.get();
        return new String[] { String.valueOf(c.getId()), c.getNombre() };
    }

    public String registrarCategoria(String nombre) {
        Categoria c = categoriaService.registrar(nombre);
        return "Categoría registrada correctamente: " + c.toString();
    }

    public String actualizarCategoria(String textoIdCategoria, String nombre) {
        int id = parsearEntero(textoIdCategoria, "ID de la Categoría");
        categoriaService.actualizarDatos(id, nombre);
        return "Categoría #" + id + " actualizada correctamente.";
    }

    public String eliminarCategoria(String textoIdCategoria) {
        int id = parsearEntero(textoIdCategoria, "ID de la Categoría");
        ResultadoEliminacion res = categoriaService.eliminar(id);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return res.getMensaje();
    }

    public List<String> obtenerProveedoresFormateados() {
        List<String> res = new ArrayList<>();
        for (Proveedor p : proveedorService.listar()) {
            res.add(p.toString());
        }
        return res;
    }

    public List<String> obtenerProveedoresCompactosParaChoice() {
        List<String> res = new ArrayList<>();
        for (Proveedor p : proveedorService.listar()) {
            String n = p.getNombre() != null ? p.getNombre() : "";
            if (n.length() > 16) {
                n = n.substring(0, 14) + "..";
            }
            res.add("[" + p.getId() + "] " + n);
        }
        return res;
    }

    public String[] obtenerDatosProveedorPorId(String textoIdProveedor) {
        int id = parsearEntero(textoIdProveedor, "ID del Proveedor");
        for (Proveedor p : proveedorService.listar()) {
            if (p.getId() != null && p.getId() == id) {
                return new String[] {
                    String.valueOf(p.getId()),
                    p.getNombre() != null ? p.getNombre() : "",
                    p.getTelefono() != null ? p.getTelefono() : "",
                    p.getCorreo() != null ? p.getCorreo() : ""
                };
            }
        }
        return null;
    }

    public String registrarProveedor(String nombre, String telefono, String correo) {
        Proveedor p = proveedorService.registrar(nombre, telefono, correo);
        return "Proveedor registrado correctamente: " + p.toString();
    }

    /** UPDATE 5: Actualización de datos de un proveedor. */
    public String actualizarProveedor(String textoIdProveedor, String nombre, String telefono, String correo) {
        int id = parsearEntero(textoIdProveedor, "ID del Proveedor");
        proveedorService.actualizarDatos(id, nombre, telefono, correo);
        return "Datos actualizados correctamente para el proveedor #" + id + ".";
    }

    public String eliminarProveedor(String textoIdProveedor) {
        int id = parsearEntero(textoIdProveedor, "ID del Proveedor");
        ResultadoEliminacion res = proveedorService.eliminar(id);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return res.getMensaje();
    }

    // =========================================================================
    // 5. FACHADA DE ESTADÍSTICAS Y REPORTES COMERCIALES (Las 15 Consultas SQL)
    // =========================================================================

    public String generarReporteCompletoConsultas(String textoParametroN) {
        int n = 2;
        if (textoParametroN != null && !textoParametroN.isBlank()) {
            n = parsearEntero(textoParametroN, "Mínimo de libros");
        }
        Map<Integer, String> mapaCategorias = new LinkedHashMap<>();
        for (Categoria c : categoriaService.listar()) {
            mapaCategorias.put(c.getId(), c.getNombre());
        }
        Map<Integer, Cliente> mapaClientes = new LinkedHashMap<>();
        for (Cliente c : clienteService.listar()) {
            mapaClientes.put(c.getId(), c);
        }
        Map<Integer, String> mapaLibros = new LinkedHashMap<>();
        for (Libro l : libroService.listarPorTitulo()) {
            mapaLibros.put(l.getId(), l.getTitulo());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("   ESTADÍSTICAS Y DATOS GENERALES DE LA LIBRERÍA\n\n");

        // Consulta 1: Listado de libros ordenados por título (SELECT, ORDER BY)
        sb.append("1. CATÁLOGO DE LIBROS ORDENADO ALFABÉTICAMENTE:\n");
        for (Libro l : libroService.listarPorTitulo()) {
            sb.append("   - ").append(l.toString()).append("\n");
        }

        // Consulta 2: Libros de una categoría determinada con stock disponible (WHERE + AND)
        sb.append("\n2. LIBROS CON STOCK DISPONIBLE POR CATEGORÍA:\n");
        for (Categoria cat : categoriaService.listar()) {
            List<Libro> disponiblesCat = libroService.listarPorCategoria(cat.getId()).stream()
                    .filter(l -> l.getStock() > 0)
                    .collect(Collectors.toList());
            for (Libro l : disponiblesCat) {
                sb.append("   - [").append(cat.getNombre()).append("] ")
                  .append(l.getTitulo()).append(" | ISBN: ").append(l.getIsbn())
                  .append(" | $").append(l.getPrecio()).append(" | Stock: ").append(l.getStock()).append("\n");
            }
        }

        // Consulta 3: Libros cuyo precio esté dentro de un rango (BETWEEN 35000 AND 65000)
        sb.append("\n3. LIBROS EN RANGO DE PRECIO INTERMEDIO ($35.000 A $65.000):\n");
        List<Libro> enRango = libroService.listarPorRangoPrecio(new BigDecimal("35000"), new BigDecimal("65000"));
        if (enRango.isEmpty()) {
            sb.append("   - (No hay libros en este rango de precio)\n");
        } else {
            for (Libro l : enRango) {
                sb.append("   - ").append(l.toString()).append("\n");
            }
        }

        // Consulta 4 (RF08): Libros con existencias inferiores a un umbral (stock < 5)
        sb.append("\n4. ALERTA DE INVENTARIO - LIBROS CON STOCK BAJO (MENOR A 5 UNIDADES):\n");
        List<Libro> stockBajo = libroService.listarStockBajo(5);
        if (stockBajo.isEmpty()) {
            sb.append("   - (Ningún libro presenta stock crítico menor a 5)\n");
        } else {
            for (Libro l : stockBajo) {
                sb.append("   - ").append(l.toString()).append("\n");
            }
        }

        // Consulta 5: Cantidad de libros registrados por categoría (COUNT + GROUP BY + LEFT JOIN)
        sb.append("\n5. CANTIDAD DE LIBROS REGISTRADOS POR CATEGORÍA:\n");
        for (Map.Entry<String, Long> e : reporteService.cantidadLibrosPorCategoria().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" libro(s)\n");
        }

        // Consulta 6: Precio promedio de los libros por categoría (AVG + GROUP BY)
        sb.append("\n6. PRECIO PROMEDIO DE LIBROS POR CATEGORÍA:\n");
        for (Map.Entry<String, BigDecimal> e : reporteService.precioPromedioPorCategoria().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": $").append(e.getValue()).append("\n");
        }

        // Consulta 7: Libro de mayor y menor precio (MAX / MIN)
        sb.append("\n7. LIBRO DE MAYOR Y MENOR PRECIO:\n");
        sb.append("   - Más caro  : ").append(reporteService.libroMasCaro().map(Libro::toString).orElse("N/A")).append("\n");
        sb.append("   - Más barato: ").append(reporteService.libroMasBarato().map(Libro::toString).orElse("N/A")).append("\n");

        // Consulta 8: Número de ventas por cliente incluyendo clientes sin compras (LEFT JOIN + COUNT)
        sb.append("\n8. NÚMERO DE VENTAS REGISTRADAS POR CLIENTE:\n");
        for (Map.Entry<String, Long> e : reporteService.numeroVentasPorCliente().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" venta(s)\n");
        }

        // Consulta 9 (9A y 9B): Total vendido por día y por mes (SUM + GROUP BY dimensión temporal)
        sb.append("\n9. INGRESOS TOTALES POR DÍA Y POR MES:\n");
        sb.append("   Por Día:\n");
        for (Map.Entry<LocalDate, BigDecimal> e : reporteService.totalVendidoPorDia().entrySet()) {
            sb.append("     * ").append(e.getKey()).append(": $").append(e.getValue()).append("\n");
        }
        sb.append("   Por Mes:\n");
        for (Map.Entry<YearMonth, BigDecimal> e : reporteService.totalVendidoPorMes().entrySet()) {
            sb.append("     * ").append(e.getKey()).append(": $").append(e.getValue()).append("\n");
        }

        // Consulta 10 (RF08): Cantidad total de unidades vendidas por libro (SUM + INNER JOIN + GROUP BY)
        sb.append("\n10. RANKING DE LIBROS MÁS VENDIDOS POR UNIDADES:\n");
        for (Map.Entry<String, Integer> e : reporteService.librosMasVendidos(15).entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" unidad(es)\n");
        }

        // Consulta 11: Listado de ventas con datos del cliente (INNER JOIN de 2 tablas: venta + cliente)
        sb.append("\n11. HISTORIAL DE VENTAS CON DATOS DEL CLIENTE:\n");
        List<Venta> todasLasVentas = ventaService.listarTodas();
        if (todasLasVentas.isEmpty()) {
            sb.append("   - (No hay ventas registradas)\n");
        } else {
            for (Venta v : todasLasVentas) {
                Cliente cl = mapaClientes.get(v.getIdCliente());
                String nombreCl = (cl != null) ? cl.getNombre() : ("Cliente #" + v.getIdCliente());
                String docCl = (cl != null) ? cl.getDocumento() : "N/A";
                sb.append("   - Venta #").append(v.getId())
                  .append(" | Fecha: ").append(v.getFecha())
                  .append(" | Cliente: ").append(nombreCl).append(" (Doc: ").append(docCl).append(")")
                  .append(" | Descuento: $").append(v.getDescuento())
                  .append(" | Total: $").append(v.getTotal()).append("\n");
            }
        }

        // Consulta 12: Detalle de ventas con información de libros y clientes (INNER JOIN de 4 tablas)
        sb.append("\n12. DETALLE DESGLOSADO DE LÍNEAS DE VENTA (CLIENTE, LIBRO Y SUBTOTAL):\n");
        if (todasLasVentas.isEmpty()) {
            sb.append("   - (No hay detalles de venta registrados)\n");
        } else {
            for (Venta v : todasLasVentas) {
                Cliente cl = mapaClientes.get(v.getIdCliente());
                String nombreCl = (cl != null) ? cl.getNombre() : ("Cliente #" + v.getIdCliente());
                for (DetalleVenta dv : v.getDetalles()) {
                    String tituloLibro = mapaLibros.getOrDefault(dv.getIdLibro(), "Libro #" + dv.getIdLibro());
                    sb.append("   - Venta #").append(v.getId())
                      .append(" (").append(v.getFecha()).append(") | Cliente: ").append(nombreCl)
                      .append(" | Libro: ").append(tituloLibro)
                      .append(" | Cant: ").append(dv.getCantidad())
                      .append(" | Unit: $").append(dv.getPrecioUnitario())
                      .append(" | Subtotal: $").append(dv.getSubtotal()).append("\n");
                }
            }
        }

        // Consulta 13: Categorías que superan N libros (GROUP BY + HAVING COUNT > N)
        sb.append("\n13. CATEGORÍAS QUE SUPERAN ").append(n).append(" LIBROS REGISTRADOS:\n");
        Map<String, Long> catsHaving = reporteService.categoriasConMasDe(n);
        if (catsHaving.isEmpty()) {
            sb.append("   - (Ninguna categoría supera ").append(n).append(" libros)\n");
        } else {
            for (Map.Entry<String, Long> e : catsHaving.entrySet()) {
                sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" libros\n");
            }
        }

        // Consulta 14: Libros que nunca han sido vendidos (LEFT JOIN + IS NULL)
        sb.append("\n14. LIBROS SIN VENTAS REGISTRADAS EN EL HISTORIAL:\n");
        List<Libro> nunca = reporteService.librosNuncaVendidos();
        if (nunca.isEmpty()) {
            sb.append("   - (Todos los libros tienen al menos una venta)\n");
        } else {
            for (Libro l : nunca) {
                sb.append("   - ").append(l.toString()).append("\n");
            }
        }

        // Consulta 15: Libros con unidades vendidas sobre el promedio (Subconsulta comparativa)
        sb.append("\n15. LIBROS CON UNIDADES VENDIDAS SUPERIORES AL PROMEDIO:\n");
        sb.append("   (Promedio de unidades por libro vendido: ")
          .append(String.format("%.2f", reporteService.promedioUnidadesPorLibroVendido())).append(")\n");
        for (Map.Entry<String, Integer> e : reporteService.librosSobrePromedioDeVentas().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" unidades\n");
        }

        return sb.toString();
    }

    // =========================================================================
    // MÉTODOS AUXILIARES PRIVADOS DE FORMATEO Y PARSEO DE ENTRADA
    // =========================================================================

    private List<String> formatearListaLibros(List<Libro> libros) {
        Map<Integer, String> mapaCats = new LinkedHashMap<>();
        for (Categoria c : categoriaService.listar()) {
            mapaCats.put(c.getId(), c.getNombre());
        }
        Map<Integer, String> mapaProvs = new LinkedHashMap<>();
        for (Proveedor p : proveedorService.listar()) {
            mapaProvs.put(p.getId(), p.getNombre());
        }
        List<String> salida = new ArrayList<>();
        for (Libro l : libros) {
            String cat = mapaCats.getOrDefault(l.getIdCategoria(), "Cat#" + l.getIdCategoria());
            String prov = mapaProvs.getOrDefault(l.getIdProveedor(), "Prov#" + l.getIdProveedor());
            salida.add("[" + l.getId() + "] " + l.getTitulo() + " | ISBN: " + l.getIsbn()
                    + " | $" + l.getPrecio() + " | Stock: " + l.getStock()
                    + " | Cat: " + cat + " | Prov: " + prov + " | Autores ID: " + l.getIdsAutores());
        }
        return salida;
    }

    private List<String> formatearListaVentas(List<Venta> ventas) {
        Map<Integer, String> mapaClientes = new LinkedHashMap<>();
        for (Cliente c : clienteService.listar()) {
            mapaClientes.put(c.getId(), c.getNombre());
        }
        Map<Integer, String> mapaLibros = new LinkedHashMap<>();
        for (Libro l : libroService.listarPorTitulo()) {
            mapaLibros.put(l.getId(), l.getTitulo());
        }
        List<String> salida = new ArrayList<>();
        for (Venta v : ventas) {
            String clienteNombre = mapaClientes.getOrDefault(v.getIdCliente(), "Cliente #" + v.getIdCliente());
            salida.add("Venta #" + v.getId() + " | Fecha: " + v.getFecha()
                    + " | Cliente: [" + v.getIdCliente() + "] " + clienteNombre
                    + " | Unidades: " + v.getTotalUnidades()
                    + " | Subtotal: $" + v.getSubtotal()
                    + " | Desc: $" + v.getDescuento()
                    + " | TOTAL: $" + v.getTotal());
            for (DetalleVenta d : v.getDetalles()) {
                String tituloLibro = mapaLibros.getOrDefault(d.getIdLibro(), "Libro #" + d.getIdLibro());
                salida.add("    -> Detalle: " + d.getCantidad() + " x " + tituloLibro
                        + " ($" + d.getPrecioUnitario() + " c/u) = $" + d.getSubtotal());
            }
        }
        return salida;
    }

    private int parsearEntero(String texto, String nombreCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El campo '" + nombreCampo + "' debe ser un número entero válido.");
        }
    }

    private BigDecimal parsearDecimal(String texto, String nombreCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        try {
            return new BigDecimal(texto.trim());
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El campo '" + nombreCampo + "' debe ser un valor numérico válido.");
        }
    }

    private LocalDate parsearFecha(String texto, String nombreCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException("El campo '" + nombreCampo + "' debe tener formato YYYY-MM-DD (ej. 2026-09-15).");
        }
    }

    private int extraerIdDeItem(String item, String nombreCampo) {
        if (item == null || item.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar un elemento válido en '" + nombreCampo + "'.");
        }
        int ini = item.indexOf('[');
        int fin = item.indexOf(']');
        if (ini >= 0 && fin > ini) {
            return parsearEntero(item.substring(ini + 1, fin), nombreCampo);
        }
        return parsearEntero(item, nombreCampo);
    }

    private List<Integer> parsearListaIds(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe indicar al menos un ID de autor (ej. 1 o 1,2).");
        }
        List<Integer> ids = new ArrayList<>();
        for (String parte : texto.split(",")) {
            if (!parte.trim().isEmpty()) {
                ids.add(parsearEntero(parte.trim(), "ID de Autor"));
            }
        }
        return ids;
    }
}

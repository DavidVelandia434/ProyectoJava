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
                ? "CONECTADO: PostgreSQL (Supabase JDBC)"
                : "MODO LOCAL: Base de Datos en Memoria (DatosPrueba)";
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
                return "Modo cambiado exitosamente a PostgreSQL (Supabase JDBC).";
            } else {
                inicializarSubsistema(false);
                return "No fue posible activar JDBC (verifique red/driver o cree las tablas SQL en Supabase). Se mantiene Modo Memoria.";
            }
        } else {
            inicializarSubsistema(false);
            return "Modo cambiado a Base de Datos en Memoria con DatosPrueba.";
        }
    }

    // =========================================================================
    // 1. FACHADA DE LIBROS (Consultas 1 a 4 + CRUD + UPDATE 1, 2 y 3)
    // =========================================================================

    public List<String> obtenerLibrosFormateados() {
        return formatearListaLibros(libroService.listarPorTitulo());
    }

    public List<String> filtrarLibrosPorCategoria(String textoIdCategoria) {
        int idCat = parsearEntero(textoIdCategoria, "ID de Categoría");
        return formatearListaLibros(libroService.listarPorCategoria(idCat));
    }

    public List<String> filtrarLibrosPorRangoPrecio(String textoMin, String textoMax) {
        BigDecimal min = parsearDecimal(textoMin, "Precio mínimo");
        BigDecimal max = parsearDecimal(textoMax, "Precio máximo");
        return formatearListaLibros(libroService.listarPorRangoPrecio(min, max));
    }

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

    /** UPDATE 1: Modificación del precio de un libro. */
    public String actualizarPrecioLibro(String textoIdLibro, String textoNuevoPrecio) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        BigDecimal nuevoPrecio = parsearDecimal(textoNuevoPrecio, "Nuevo precio");
        libroService.actualizarPrecio(idLibro, nuevoPrecio);
        return "UPDATE 1 ejecutado: Precio actualizado a $" + nuevoPrecio + " para el libro #" + idLibro + ".";
    }

    /** UPDATE 2: Corrección del título de un libro. */
    public String corregirTituloLibro(String textoIdLibro, String nuevoTitulo) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        libroService.corregirTitulo(idLibro, nuevoTitulo);
        return "UPDATE 2 ejecutado: Título corregido para el libro #" + idLibro + ".";
    }

    /** UPDATE 3: Ajuste autorizado de existencias con motivo. */
    public String ajustarStockLibro(String textoIdLibro, String textoNuevoStock, String motivo) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        int nuevoStock = parsearEntero(textoNuevoStock, "Nuevo stock");
        libroService.ajustarStock(idLibro, nuevoStock, motivo);
        return "UPDATE 3 ejecutado: Stock del libro #" + idLibro + " ajustado a " + nuevoStock + " (Motivo: " + motivo + ").";
    }

    public String eliminarLibro(String textoIdLibro) {
        int idLibro = parsearEntero(textoIdLibro, "ID del Libro");
        ResultadoEliminacion res = libroService.eliminar(idLibro);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return res.getMensaje();
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

    public List<String> obtenerTodasLasVentasFormateadas() {
        return formatearListaVentas(ventaService.listarTodas());
    }

    public List<String> obtenerVentasPorCliente(String itemCliente) {
        int idCliente = extraerIdDeItem(itemCliente, "Cliente");
        return formatearListaVentas(ventaService.historialPorCliente(idCliente));
    }

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

    public String registrarCliente(String nombre, String documento, String telefono, String correo) {
        Cliente c = clienteService.registrar(nombre, documento, telefono, correo);
        return "Cliente registrado correctamente: " + c.toString();
    }

    /** UPDATE 4: Modificación de datos de contacto de un cliente. */
    public String actualizarContactoCliente(String textoIdCliente, String nombre, String telefono, String correo) {
        int idCliente = parsearEntero(textoIdCliente, "ID del Cliente");
        clienteService.actualizarContacto(idCliente, nombre, telefono, correo);
        return "UPDATE 4 ejecutado: Datos de contacto actualizados para el cliente #" + idCliente + ".";
    }

    public String eliminarCliente(String textoIdCliente) {
        int idCliente = parsearEntero(textoIdCliente, "ID del Cliente");
        ResultadoEliminacion res = clienteService.eliminar(idCliente);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return res.getMensaje();
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

    public String registrarAutor(String nombre, String nacionalidad) {
        Autor a = autorService.registrar(nombre, nacionalidad);
        return "Autor registrado: " + a.toString();
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

    public String registrarCategoria(String nombre) {
        Categoria c = categoriaService.registrar(nombre);
        return "Categoría registrada: " + c.toString();
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

    public String registrarProveedor(String nombre, String telefono, String correo) {
        Proveedor p = proveedorService.registrar(nombre, telefono, correo);
        return "Proveedor registrado: " + p.toString();
    }

    /** UPDATE 5: Actualización de datos de un proveedor. */
    public String actualizarProveedor(String textoIdProveedor, String nombre, String telefono, String correo) {
        int id = parsearEntero(textoIdProveedor, "ID del Proveedor");
        proveedorService.actualizarDatos(id, nombre, telefono, correo);
        return "UPDATE 5 ejecutado: Datos actualizados para el proveedor #" + id + ".";
    }

    public String eliminarProveedor(String textoIdProveedor) {
        int id = parsearEntero(textoIdProveedor, "ID del Proveedor");
        ResultadoEliminacion res = proveedorService.eliminar(id);
        if (res != ResultadoEliminacion.ELIMINADO) {
            throw new ReglaNegocioException(res.getMensaje());
        }
        return res.getMensaje();
    }

    // 5. FACHADA DE CONSULTAS Y REPORTES COMERCIALES (15 Consultas Obligatorias)

    public String generarReporteCompletoConsultas(String textoParametroN) {
        int n = 2;
        if (textoParametroN != null && !textoParametroN.isBlank()) {
            n = parsearEntero(textoParametroN, "Parámetro N");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("   REPORTE EJECUTIVO DE LAS 15 CONSULTAS OBLIGATORIAS DEL SISTEMA\n");

        sb.append("1. LISTADO DE LIBROS ORDENADOS POR TÍTULO (Consulta 1):\n");
        for (Libro l : libroService.listarPorTitulo()) {
            sb.append("   - ").append(l.toString()).append("\n");
        }

        sb.append("\n5. CANTIDAD DE LIBROS REGISTRADOS POR CATEGORÍA (Consulta 5 - COUNT + GROUP BY):\n");
        for (Map.Entry<String, Long> e : reporteService.cantidadLibrosPorCategoria().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" libro(s)\n");
        }

        sb.append("\n6. PRECIO PROMEDIO DE LOS LIBROS POR CATEGORÍA (Consulta 6 - AVG + GROUP BY):\n");
        for (Map.Entry<String, BigDecimal> e : reporteService.precioPromedioPorCategoria().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": $").append(e.getValue()).append("\n");
        }

        sb.append("\n7. LIBRO DE MAYOR Y MENOR PRECIO (Consulta 7 - MAX / MIN):\n");
        sb.append("   - Más caro  : ").append(reporteService.libroMasCaro().map(Libro::toString).orElse("N/A")).append("\n");
        sb.append("   - Más barato: ").append(reporteService.libroMasBarato().map(Libro::toString).orElse("N/A")).append("\n");

        sb.append("\n8. NÚMERO DE VENTAS POR CLIENTE (Consulta 8 - LEFT JOIN + COUNT):\n");
        for (Map.Entry<String, Long> e : reporteService.numeroVentasPorCliente().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" venta(s)\n");
        }

        sb.append("\n9A. TOTAL VENDIDO POR DÍA (Consulta 9 - SUM + GROUP BY fecha):\n");
        for (Map.Entry<LocalDate, BigDecimal> e : reporteService.totalVendidoPorDia().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": $").append(e.getValue()).append("\n");
        }

        sb.append("\n9B. TOTAL VENDIDO POR MES (Consulta 9 - SUM + GROUP BY mes):\n");
        for (Map.Entry<YearMonth, BigDecimal> e : reporteService.totalVendidoPorMes().entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": $").append(e.getValue()).append("\n");
        }

        sb.append("\n10. TOP LIBROS MÁS VENDIDOS POR UNIDADES (Consulta 10 / RF08):\n");
        for (Map.Entry<String, Integer> e : reporteService.librosMasVendidos(10).entrySet()) {
            sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" unidad(es)\n");
        }

        sb.append("\n13. CATEGORÍAS QUE SUPERAN ").append(n).append(" LIBROS (Consulta 13 - GROUP BY + HAVING):\n");
        Map<String, Long> catsHaving = reporteService.categoriasConMasDe(n);
        if (catsHaving.isEmpty()) {
            sb.append("   - (Ninguna categoría supera ").append(n).append(" libros)\n");
        } else {
            for (Map.Entry<String, Long> e : catsHaving.entrySet()) {
                sb.append("   - ").append(e.getKey()).append(": ").append(e.getValue()).append(" libros\n");
            }
        }

        sb.append("\n14. LIBROS QUE NUNCA HAN SIDO VENDIDOS (Consulta 14 - LEFT JOIN IS NULL):\n");
        List<Libro> nunca = reporteService.librosNuncaVendidos();
        if (nunca.isEmpty()) {
            sb.append("   - (Todos los libros tienen al menos una venta)\n");
        } else {
            for (Libro l : nunca) {
                sb.append("   - ").append(l.toString()).append("\n");
            }
        }

        sb.append("\n15. LIBROS CON UNIDADES VENDIDAS SOBRE EL PROMEDIO (Consulta 15 - Subconsulta comparativa):\n");
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

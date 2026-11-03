package libreria;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;
import libreria.dao.AutorDAO;
import libreria.dao.CategoriaDAO;
import libreria.dao.ClienteDAO;
import libreria.dao.LibroDAO;
import libreria.dao.ProveedorDAO;
import libreria.dao.VentaRepository;
import libreria.dao.memoria.AutorDAOMemoria;
import libreria.dao.memoria.CategoriaDAOMemoria;
import libreria.dao.memoria.ClienteDAOMemoria;
import libreria.dao.memoria.LibroDAOMemoria;
import libreria.dao.memoria.ProveedorDAOMemoria;
import libreria.dao.memoria.VentaRepositoryMemoria;
import libreria.database.BaseDatosEnMemoria;
import libreria.database.DatosPrueba;
import libreria.service.AutorService;
import libreria.service.CategoriaService;
import libreria.service.ClienteService;
import libreria.service.DescuentoPorVolumen;
import libreria.service.LibroService;
import libreria.service.PoliticaDescuento;
import libreria.service.ProveedorService;
import libreria.service.ReporteService;
import libreria.service.VentaService;
import libreria.ui.ConsolaUtil;
import libreria.ui.MenuCatalogo;
import libreria.ui.MenuClientes;
import libreria.ui.MenuLibros;
import libreria.ui.MenuPrincipal;
import libreria.ui.MenuReportes;
import libreria.ui.MenuVentas;

/** Punto de entrada: aquí se "ensamblan" las dependencias (inyección por constructor). */
public class Main {
    public static void main(String[] args) {
        // ==========================================================
        //  1) CAPA DE PERSISTENCIA  (única parte que cambia con JDBC)
        // ==========================================================
        BaseDatosEnMemoria bd = new BaseDatosEnMemoria();
        AutorDAO autorDAO = new AutorDAOMemoria(bd);
        CategoriaDAO categoriaDAO = new CategoriaDAOMemoria(bd);
        ProveedorDAO proveedorDAO = new ProveedorDAOMemoria(bd);
        ClienteDAO clienteDAO = new ClienteDAOMemoria(bd);
        LibroDAO libroDAO = new LibroDAOMemoria(bd);
        VentaRepository ventaRepo = new VentaRepositoryMemoria(bd, libroDAO);

        // TODO (cuando exista la base de datos): reemplazar las líneas de arriba por algo como:
        //   ConexionBD conexion = new ConexionBD();
        //   LibroDAO libroDAO = new LibroDAOJdbc(conexion);
        //   VentaRepository ventaRepo = new VentaRepositoryJdbc(conexion);
        //   ... y crear el resto de DAO JDBC (autor, categoría, proveedor, cliente).
        // Con la base real los datos vienen del script sql/02_poblar.sql, no de DatosPrueba.

        boolean cargarDatosPrueba = true;
        if (cargarDatosPrueba) {
            DatosPrueba.cargar(autorDAO, categoriaDAO, proveedorDAO, clienteDAO, libroDAO, ventaRepo);
        }

        // ==========================================================
        //  2) CAPA DE NEGOCIO
        // ==========================================================
        PoliticaDescuento politica = new DescuentoPorVolumen(5, new BigDecimal("0.10"));
        LibroService libroService = new LibroService(libroDAO, categoriaDAO, proveedorDAO, autorDAO, ventaRepo);
        AutorService autorService = new AutorService(autorDAO, libroDAO);
        CategoriaService categoriaService = new CategoriaService(categoriaDAO, libroDAO);
        ProveedorService proveedorService = new ProveedorService(proveedorDAO, libroDAO);
        ClienteService clienteService = new ClienteService(clienteDAO, ventaRepo);
        VentaService ventaService = new VentaService(ventaRepo, libroDAO, clienteDAO, politica);
        ReporteService reporteService = new ReporteService(libroDAO, categoriaDAO, clienteDAO, ventaRepo);

        // ==========================================================
        //  3) INTERFAZ
        // ==========================================================
        ConsolaUtil consola = new ConsolaUtil(new Scanner(System.in));
        MenuPrincipal menu = new MenuPrincipal(consola,
                new MenuLibros(consola, libroService, categoriaService, proveedorService, autorService),
                new MenuCatalogo(consola, autorService, categoriaService, proveedorService),
                new MenuClientes(consola, clienteService),
                new MenuVentas(consola, ventaService, libroService, clienteService),
                new MenuReportes(consola, reporteService));
        try {
            menu.ejecutar();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Hasta pronto.");
        } catch (RuntimeException e) {
            System.out.println("Ocurrió un error inesperado. Intente de nuevo.");
        }
    }
}

/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ConfiguracionLibreria.java
 * Fecha    : 06/11/2026
 */
package libreria.api.config;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import libreria.dao.AutorDAO;
import libreria.dao.CategoriaDAO;
import libreria.dao.ClienteDAO;
import libreria.dao.LibroDAO;
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
import libreria.service.AutorService;
import libreria.service.CategoriaService;
import libreria.service.ClienteService;
import libreria.service.DescuentoPorVolumen;
import libreria.service.LibroService;
import libreria.service.PoliticaDescuento;
import libreria.service.ProveedorService;
import libreria.service.ReporteService;
import libreria.service.VentaService;

/**
 * =======================================================
 * PUNTO 2 DE CONEXIÓN A LA BASE DE DATOS EN LA API REST
 * =======================================================
 * Ensambla los Beans de Spring Boot en la rama Second-update.
 * Conecta ConexionBD (Supabase PostgreSQL) con los DAOs JDBC cuando la base de
 * datos responde y tiene las tablas creadas, o usa BaseDatosEnMemoria como respaldo.
 */
@Configuration
public class ConfiguracionLibreria {

    // ===== 1) CONEXIÓN JDBC A SUPABASE POSTGRESQL =====
    @Bean
    public ConexionBD conexionBD(
            @Value("${libreria.db.url:" + ConexionBD.DEFAULT_SUPABASE_URL + "}") String url,
            @Value("${libreria.db.user:" + ConexionBD.DEFAULT_USER + "}") String user,
            @Value("${libreria.db.password:" + ConexionBD.DEFAULT_PASSWORD + "}") String password) {
        return new ConexionBD(url, user, password);
    }

    @Bean
    public BaseDatosEnMemoria baseDatos() {
        return new BaseDatosEnMemoria();
    }

    @Bean
    public AutorDAO autorDAO(ConexionBD conexionBD, BaseDatosEnMemoria bd,
                             @Value("${libreria.db.modo:auto}") String modo) {
        if (usarJdbc(conexionBD, modo)) return new AutorDAOJdbc(conexionBD);
        return new AutorDAOMemoria(bd);
    }

    @Bean
    public CategoriaDAO categoriaDAO(ConexionBD conexionBD, BaseDatosEnMemoria bd,
                                     @Value("${libreria.db.modo:auto}") String modo) {
        if (usarJdbc(conexionBD, modo)) return new CategoriaDAOJdbc(conexionBD);
        return new CategoriaDAOMemoria(bd);
    }

    @Bean
    public ProveedorDAO proveedorDAO(ConexionBD conexionBD, BaseDatosEnMemoria bd,
                                     @Value("${libreria.db.modo:auto}") String modo) {
        if (usarJdbc(conexionBD, modo)) return new ProveedorDAOJdbc(conexionBD);
        return new ProveedorDAOMemoria(bd);
    }

    @Bean
    public ClienteDAO clienteDAO(ConexionBD conexionBD, BaseDatosEnMemoria bd,
                                 @Value("${libreria.db.modo:auto}") String modo) {
        if (usarJdbc(conexionBD, modo)) return new ClienteDAOJdbc(conexionBD);
        return new ClienteDAOMemoria(bd);
    }

    @Bean
    public LibroDAO libroDAO(ConexionBD conexionBD, BaseDatosEnMemoria bd,
                             @Value("${libreria.db.modo:auto}") String modo) {
        if (usarJdbc(conexionBD, modo)) return new LibroDAOJdbc(conexionBD);
        return new LibroDAOMemoria(bd);
    }

    @Bean
    public VentaRepository ventaRepository(ConexionBD conexionBD, BaseDatosEnMemoria bd, LibroDAO libroDAO,
                                           @Value("${libreria.db.modo:auto}") String modo) {
        if (usarJdbc(conexionBD, modo)) return new VentaRepositoryJdbc(conexionBD);
        return new VentaRepositoryMemoria(bd, libroDAO);
    }

    private boolean usarJdbc(ConexionBD conexionBD, String modo) {
        if ("memoria".equalsIgnoreCase(modo)) return false;
        if ("jdbc".equalsIgnoreCase(modo)) return true;
        return conexionBD.probarConexionYEsquema();
    }

    @Bean
    public ApplicationRunner cargarDatosPrueba(@Value("${libreria.datos-prueba:true}") boolean cargar,
                                               ConexionBD conexionBD,
                                               @Value("${libreria.db.modo:auto}") String modo,
                                               AutorDAO autorDAO, CategoriaDAO categoriaDAO,
                                               ProveedorDAO proveedorDAO, ClienteDAO clienteDAO,
                                               LibroDAO libroDAO, VentaRepository ventaRepository) {
        return args -> {
            // Solo carga DatosPrueba en memoria; en PostgreSQL se usa sql/02_poblar.sql
            if (cargar && !usarJdbc(conexionBD, modo)) {
                DatosPrueba.cargar(autorDAO, categoriaDAO, proveedorDAO, clienteDAO, libroDAO, ventaRepository);
            }
        };
    }

    // ===== 2) CAPA DE NEGOCIO (SERVICIOS) =====
    @Bean
    public PoliticaDescuento politicaDescuento() {
        return new DescuentoPorVolumen(5, new BigDecimal("0.10"));
    }

    @Bean
    public LibroService libroService(LibroDAO libroDAO, CategoriaDAO categoriaDAO, ProveedorDAO proveedorDAO,
                                     AutorDAO autorDAO, VentaRepository ventaRepository) {
        return new LibroService(libroDAO, categoriaDAO, proveedorDAO, autorDAO, ventaRepository);
    }

    @Bean
    public AutorService autorService(AutorDAO autorDAO, LibroDAO libroDAO) {
        return new AutorService(autorDAO, libroDAO);
    }

    @Bean
    public CategoriaService categoriaService(CategoriaDAO categoriaDAO, LibroDAO libroDAO) {
        return new CategoriaService(categoriaDAO, libroDAO);
    }

    @Bean
    public ProveedorService proveedorService(ProveedorDAO proveedorDAO, LibroDAO libroDAO) {
        return new ProveedorService(proveedorDAO, libroDAO);
    }

    @Bean
    public ClienteService clienteService(ClienteDAO clienteDAO, VentaRepository ventaRepository) {
        return new ClienteService(clienteDAO, ventaRepository);
    }

    @Bean
    public VentaService ventaService(VentaRepository ventaRepository, LibroDAO libroDAO, ClienteDAO clienteDAO,
                                    PoliticaDescuento politica) {
        return new VentaService(ventaRepository, libroDAO, clienteDAO, politica);
    }

    @Bean
    public ReporteService reporteService(LibroDAO libroDAO, CategoriaDAO categoriaDAO, ClienteDAO clienteDAO,
                                          VentaRepository ventaRepository) {
        return new ReporteService(libroDAO, categoriaDAO, clienteDAO, ventaRepository);
    }
}

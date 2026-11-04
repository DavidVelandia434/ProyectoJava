/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ConfiguracionLibreria.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
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

/**
 * Equivale al "ensamblado" de Main.java, pero con beans de Spring.
 * Las clases de service/dao no llevan anotaciones de Spring: se registran aquí.
 *
 * ESPACIO PARA LA BASE REAL: cuando existan las clases dao.jdbc, solo se cambian
 * los beans de la sección 1 (por ejemplo new LibroDAOJdbc(new ConexionBD())).
 */
@Configuration
public class ConfiguracionLibreria {

    // ===== 1) PERSISTENCIA (única parte que cambia con JDBC) =====
    @Bean
    public BaseDatosEnMemoria baseDatos() { return new BaseDatosEnMemoria(); }

    @Bean
    public AutorDAO autorDAO(BaseDatosEnMemoria bd) { return new AutorDAOMemoria(bd); }

    @Bean
    public CategoriaDAO categoriaDAO(BaseDatosEnMemoria bd) { return new CategoriaDAOMemoria(bd); }

    @Bean
    public ProveedorDAO proveedorDAO(BaseDatosEnMemoria bd) { return new ProveedorDAOMemoria(bd); }

    @Bean
    public ClienteDAO clienteDAO(BaseDatosEnMemoria bd) { return new ClienteDAOMemoria(bd); }

    @Bean
    public LibroDAO libroDAO(BaseDatosEnMemoria bd) { return new LibroDAOMemoria(bd); }

    @Bean
    public VentaRepository ventaRepository(BaseDatosEnMemoria bd, LibroDAO libroDAO) {
        return new VentaRepositoryMemoria(bd, libroDAO);
    }

    @Bean
    public ApplicationRunner cargarDatosPrueba(@Value("${libreria.datos-prueba:true}") boolean cargar,
                                               AutorDAO autorDAO, CategoriaDAO categoriaDAO,
                                               ProveedorDAO proveedorDAO, ClienteDAO clienteDAO,
                                               LibroDAO libroDAO, VentaRepository ventaRepository) {
        return args -> {
            if (cargar) {
                DatosPrueba.cargar(autorDAO, categoriaDAO, proveedorDAO, clienteDAO, libroDAO, ventaRepository);
            }
        };
    }

    // ===== 2) NEGOCIO =====
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

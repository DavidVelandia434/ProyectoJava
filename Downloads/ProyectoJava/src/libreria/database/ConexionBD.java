/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ConexionBD.java
 * Fecha    : 06/11/2026
 */
package libreria.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import libreria.dao.PersistenciaException;

/**
 * =======================================================
 * PUNTO 1 DE CONEXIÓN A LA BASE DE DATOS (JDBC - SUPABASE)
 * =======================================================
 * Gestiona la conexión a PostgreSQL (Supabase) mediante JDBC puro.
 *
 * Prioridad de lectura de credenciales:
 * 1) Variables de entorno (LIBRERIA_DB_URL, LIBRERIA_DB_USER, LIBRERIA_DB_PASSWORD)
 * 2) Parámetros inyectados por constructor (ej. desde application.properties en ConfiguracionLibreria)
 * 3) Configuración por defecto hacia el servidor PostgreSQL de Supabase del proyecto.
 */
public class ConexionBD {

    // URL JDBC hacia Supabase PostgreSQL con sslmode=require (obligatorio en Supabase)
    public static final String DEFAULT_SUPABASE_URL =
            "jdbc:postgresql://db.xuzytwhjltrkgbgabpzy.supabase.co:5432/postgres"
            + "?user=postgres&password=playynintendonosejueganseemulan&sslmode=require&connectTimeout=8";

    public static final String DEFAULT_HOST_URL =
            "jdbc:postgresql://db.xuzytwhjltrkgbgabpzy.supabase.co:5432/postgres?sslmode=require&connectTimeout=8";

    public static final String DEFAULT_USER = "postgres";
    public static final String DEFAULT_PASSWORD = "playynintendonosejueganseemulan";

    private final String url;
    private final String usuario;
    private final String clave;

    static {
        try {
            // Carga explícita del driver de PostgreSQL si está presente en el classpath
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
            // En JDBC 4.0+ DriverManager detecta el driver automáticamente si el JAR está en el classpath.
        }
    }

    public ConexionBD() {
        this(
            leerEnvOValor("LIBRERIA_DB_URL", DEFAULT_SUPABASE_URL),
            leerEnvOValor("LIBRERIA_DB_USER", DEFAULT_USER),
            leerEnvOValor("LIBRERIA_DB_PASSWORD", DEFAULT_PASSWORD)
        );
    }

    public ConexionBD(String url, String usuario, String clave) {
        this.url = (url == null || url.isBlank()) ? DEFAULT_SUPABASE_URL : url.trim();
        this.usuario = (usuario == null || usuario.isBlank()) ? DEFAULT_USER : usuario.trim();
        this.clave = (clave == null || clave.isBlank()) ? DEFAULT_PASSWORD : clave;
    }

    private static String leerEnvOValor(String nombreVariable, String valorPorDefecto) {
        String valor = System.getenv(nombreVariable);
        return (valor != null && !valor.isBlank()) ? valor.trim() : valorPorDefecto;
    }

    /**
     * Obtiene una conexión activa hacia PostgreSQL (Supabase).
     * Úsese siempre con try-with-resources:
     * try (Connection con = conexionBD.obtenerConexion()) { ... }
     */
    public Connection obtenerConexion() {
        if (url == null || url.isBlank()) {
            throw new PersistenciaException(
                "La conexión a la base de datos no está configurada. Revise el manual técnico.");
        }
        try {
            Properties props = new Properties();
            if (usuario != null && !usuario.isBlank()) {
                props.setProperty("user", usuario);
            }
            if (clave != null && !clave.isBlank()) {
                props.setProperty("password", clave);
            }
            if (!url.contains("sslmode=")) {
                props.setProperty("sslmode", "require");
            }
            props.setProperty("connectTimeout", "8");
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            // Cumple RNF: no expone contraseñas ni trazas sensibles al usuario final
            throw new PersistenciaException("No fue posible conectar con la base de datos PostgreSQL (Supabase).", e);
        }
    }

    /** Verifica si el servidor responde y si las tablas del esquema ya fueron creadas. */
    public boolean probarConexionYEsquema() {
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM categoria");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    /** Devuelve un diagnóstico legible (sin exponer la contraseña) para la interfaz o consola. */
    public String diagnosticarConexion() {
        String urlSegura = url.replaceAll("password=[^&]+", "password=********");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            return "[ADVERTENCIA] No se encontró el driver org.postgresql.Driver en el classpath.\n"
                 + "URL configurada: " + urlSegura + "\n"
                 + "Solución: Agregue postgresql-42.7.x.jar en la carpeta lib/ o ejecute con Maven (pom.xml).";
        }
        try (Connection con = obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM libro");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "[CONECTADO A SUPABASE] Conexión JDBC exitosa y esquema verificado.\n"
                         + "Servidor: db.xuzytwhjltrkgbgabpzy.supabase.co:5432/postgres\n"
                         + "Libros registrados en BD remota: " + rs.getInt(1);
                }
            } catch (SQLException exTabla) {
                return "[CONECTADO SIN TABLAS] Se conectó a Supabase correctamente, pero las tablas aún no existen.\n"
                     + "Solución: Ejecute los scripts sql/01_crear_tablas.sql y sql/02_poblar.sql en el SQL Editor de Supabase.";
            }
            return "[CONECTADO A SUPABASE] Conexión establecida con éxito.";
        } catch (PersistenciaException | SQLException e) {
            Throwable causa = e.getCause();
            String detalle = (causa != null && causa.getMessage() != null) ? causa.getMessage() : e.getMessage();
            detalle = detalle.replace(clave, "********");
            return "[MODO MEMORIA ACTIVO] No se pudo establecer conexión con Supabase.\n"
                 + "URL configurada: " + urlSegura + "\n"
                 + "Causa reportada: " + detalle + "\n"
                 + "Nota: Si su red usa IPv4 únicamente, use el Connection Pooler (Supavisor) de Supabase en el puerto 6543 o 5432.";
        }
    }
}

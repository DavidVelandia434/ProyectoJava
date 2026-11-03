/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ConexionBD.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import libreria.dao.PersistenciaException;

/**
 * ============================================================
 *  ESPACIO PARA LA CONEXIÓN REAL (JDBC)
 * ============================================================
 * Las credenciales NO se escriben en el código: se leen de variables de entorno.
 *
 *   LIBRERIA_DB_URL       ej. jdbc:mysql://localhost:3306/libreria
 *                              jdbc:postgresql://localhost:5432/libreria
 *   LIBRERIA_DB_USER      usuario de la base de datos
 *   LIBRERIA_DB_PASSWORD  contraseña
 *
 * Además hay que agregar el driver (JAR de MySQL Connector/J o PostgreSQL) al classpath.
 */
public class ConexionBD {
    private final String url;
    private final String usuario;
    private final String clave;

    public ConexionBD() {
        this(System.getenv("LIBRERIA_DB_URL"),
             System.getenv("LIBRERIA_DB_USER"),
             System.getenv("LIBRERIA_DB_PASSWORD"));
    }

    public ConexionBD(String url, String usuario, String clave) {
        this.url = url;
        this.usuario = usuario;
        this.clave = clave;
    }

    /** Úsese con try-with-resources: try (Connection con = conexionBD.obtenerConexion()) { ... } */
    public Connection obtenerConexion() {
        if (url == null || url.isBlank()) {
            throw new PersistenciaException(
                    "La conexión a la base de datos no está configurada. Revise el manual técnico.");
        }
        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            // Mensaje genérico: no se exponen contraseñas ni detalles técnicos al usuario.
            throw new PersistenciaException("No fue posible conectar con la base de datos.", e);
        }
    }
}

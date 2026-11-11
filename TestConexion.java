import libreria.database.ConexionBD;
import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            if (conn != null) {
                System.out.println("¡Conexión a PostgreSQL establecida con éxito!");
            }
        } catch (Exception e) {
            System.err.println("Fallo al conectar a la base de datos:");
            e.printStackTrace();
        }
    }
}
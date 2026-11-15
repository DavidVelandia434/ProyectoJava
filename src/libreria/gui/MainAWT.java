/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MainAWT.java
 * Paquete  : libreria.gui (Carpeta independiente para la Fachada y la Interfaz Gráfica AWT)
 * Fecha    : 06/11/2026
 */
package libreria.gui;

import java.awt.EventQueue;

/**
 * PUNTO DE ENTRADA DE LA INTERFAZ GRÁFICA AWT (EJECUTABLE EN VS CODE)
 * =========================================================================
 * Cómo ejecutar en Visual Studio Code:
 * 1. Abra este archivo (src/libreria/gui/MainAWT.java).
 * 2. Haga clic en "Run" sobre el método main() o presione F5.
 *
 * No requiere servidores externos para arrancar: LibreriaFacade intenta
 * conectar a PostgreSQL (Supabase) automáticamente y, si no hay red o las
 * tablas aún no existen, inicia en Modo Memoria con DatosPrueba.
 */
public class MainAWT {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            LibreriaFacade facade = new LibreriaFacade();
            VentanaPrincipalAWT ventana = new VentanaPrincipalAWT(facade);
            ventana.setVisible(true);
        });
    }
}

/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MenuPrincipal.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

public class MenuPrincipal {
    private final ConsolaUtil c;
    private final MenuLibros menuLibros;
    private final MenuCatalogo menuCatalogo;
    private final MenuClientes menuClientes;
    private final MenuVentas menuVentas;
    private final MenuReportes menuReportes;

    public MenuPrincipal(ConsolaUtil c, MenuLibros menuLibros, MenuCatalogo menuCatalogo,
                         MenuClientes menuClientes, MenuVentas menuVentas, MenuReportes menuReportes) {
        this.c = c;
        this.menuLibros = menuLibros;
        this.menuCatalogo = menuCatalogo;
        this.menuClientes = menuClientes;
        this.menuVentas = menuVentas;
        this.menuReportes = menuReportes;
    }

    public void ejecutar() {
        int op;
        do {
            ConsolaUtil.titulo("SISTEMA DE GESTIÓN DE LIBRERÍA");
            System.out.println("1. Libros\n2. Autores, categorías y proveedores\n3. Clientes\n"
                    + "4. Ventas\n5. Reportes\n0. Salir");
            op = c.leerEntero("Opción:");
            switch (op) {
                case 1: menuLibros.ejecutar(); break;
                case 2: menuCatalogo.ejecutar(); break;
                case 3: menuClientes.ejecutar(); break;
                case 4: menuVentas.ejecutar(); break;
                case 5: menuReportes.ejecutar(); break;
                case 0: System.out.println("Hasta pronto."); break;
                default: System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }
}

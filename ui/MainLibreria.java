package ui;

import dao.LibroDAO;
import dao.LibroDAOMemoria;
import service.LibroService;
import vista.InOut;

public class MainLibreria {
    
    public static void main(String[] args) {
        
        InOut vista = new InOut();
        
        // INYECCIÓN DE DEPENDENCIAS: 
        // Cuando conectemos la BD, solo cambiaremos "new LibroDAOMemoria()" por "new LibroDAOImpl()" aquí. 
        // El resto del programa (Service, View, Controller) quedará intacto.
        LibroDAO repositorio = new LibroDAOMemoria();
        LibroService servicio = new LibroService(repositorio);
        
        int opcion;
        
        do {
            
            vista.imprimirMensaje("SISTEMA DE GESTIÓN LIBRERÍA (PROTOTIPO)");
            vista.imprimirMensaje("1. Registrar nuevo libro");
            vista.imprimirMensaje("2. Consultar inventario completo");
            vista.imprimirMensaje("3. Salir");
            
            opcion = vista.leerEntero("Seleccione una operación: ");
            
            switch (opcion) {
                
                case 1:
                    String isbn = vista.leerString("Ingrese ISBN: ");
                    String titulo = vista.leerString("Ingrese Título: ");
                    double precio = vista.leerDouble("Ingrese Precio de Venta: ");
                    int stock = vista.leerEntero("Ingrese Existencias iniciales: ");
                    
                    String respuesta = servicio.registrarLibro(isbn, titulo, precio, stock);
                    vista.imprimirMensaje(respuesta);
                    break;
                    
                case 2:
                    vista.imprimirMensaje("CATÁLOGO ACTUAL");
                    vista.imprimirMensaje(servicio.mostrarInventario());
                    break;
                    
                case 3:
                    vista.imprimirMensaje("Cerrando el sistema");
                    break;
                    
                default:
                    vista.imprimirMensaje("Opción no válida.");
                    break;
                    
            }
            
        } while (opcion != 3);
        
    }
    
}
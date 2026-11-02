package service;

import dao.LibroDAO;
import java.util.List;
import model.Libro;

public class LibroService {
    
    // Dependemos de la abstracción (LibroDAO), no de la memoria ni de MySQL directamente
    private LibroDAO libroDAO;
    
    public LibroService(LibroDAO libroDAO) {
        
        this.libroDAO = libroDAO;
        
    }
    
    public String registrarLibro(String isbn, String titulo, double precio, int existencias) {
        
        if (libroDAO.buscarPorIsbn(isbn) != null) {
            
            return "Error: Ya existe un libro registrado con el ISBN " + isbn;
            
        }
        
        if (precio < 0 || existencias < 0) {
            
            return "Error: Valores comerciales inválidos.";
            
        }
        
        Libro nuevoLibro = new Libro(isbn, titulo, precio, existencias);
        libroDAO.crear(nuevoLibro);
        
        return "Libro registrado exitosamente en el catálogo.";
        
    }
    
    public String mostrarInventario() {
        
        List<Libro> catalogo = libroDAO.listarTodos();
        
        if (catalogo.isEmpty()) {
            
            return "El inventario está vacío.";
            
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (Libro l : catalogo) {
            
            sb.append(l.toString()).append("\n");
            
        }
        
        return sb.toString();
        
    }
    
}
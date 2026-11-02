package dao;

import java.util.ArrayList;
import java.util.List;
import model.Libro;

public class LibroDAOMemoria implements LibroDAO {
    
    // Estructura temporal. 
    // TODO: En la próxima entrega, crear 'LibroDAOImpl' que implemente LibroDAO usando JDBC, Connection y PreparedStatement.
    private List<Libro> bdSimulada;
    
    public LibroDAOMemoria() {
        
        this.bdSimulada = new ArrayList<>();
        
    }
    
    @Override
    public void crear(Libro libro) {
        
        // TODO: A futuro será un -> INSERT INTO libro (isbn, titulo, precio, stock) VALUES (?, ?, ?, ?)
        bdSimulada.add(libro);
        
    }
    
    @Override
    public Libro buscarPorIsbn(String isbn) {
        
        // TODO: A futuro será un -> SELECT * FROM libro WHERE isbn = ?
        for (Libro l : bdSimulada) {
            
            if (l.getIsbn().equalsIgnoreCase(isbn)) {
                
                return l;
                
            }
            
        }
        
        return null;
        
    }
    
    @Override
    public List<Libro> listarTodos() {
        
        // TODO: A futuro será un -> SELECT * FROM libro ORDER BY titulo
        return new ArrayList<>(bdSimulada);
        
    }
    
    @Override
    public void actualizar(Libro libro) {
        
        // TODO: A futuro será un -> UPDATE libro SET precio = ?, stock = ? WHERE isbn = ?
        Libro existente = buscarPorIsbn(libro.getIsbn());
        
        if (existente != null) {
            
            existente.setPrecio(libro.getPrecio());
            existente.setExistencias(libro.getExistencias());
            
        }
        
    }
    
    @Override
    public void eliminar(String isbn) {
        
        // TODO: A futuro será un -> DELETE FROM libro WHERE isbn = ?
        Libro existente = buscarPorIsbn(isbn);
        
        if (existente != null) {
            
            bdSimulada.remove(existente);
            
        }
        
    }
    
}
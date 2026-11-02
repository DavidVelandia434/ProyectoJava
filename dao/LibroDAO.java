package dao;

import java.util.List;
import model.Libro;

// Contrato para base de datos 
public interface LibroDAO {
    
    public abstract void crear(Libro libro);
    
    public abstract Libro buscarPorIsbn(String isbn);
    
    public abstract List<Libro> listarTodos();
    
    public abstract void actualizar(Libro libro);
    
    public abstract void eliminar(String isbn);
    
}
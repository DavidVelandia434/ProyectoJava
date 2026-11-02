package model;

public class Libro {
    
    //estos atributos mapearán directamente a las columnas de la tabla 'Libro' en la BD
    private String isbn;
    private String titulo;
    private double precio;
    private int existencias;
    
    public Libro(String isbn, String titulo, double precio, int existencias) {
        
        this.isbn = isbn;
        this.titulo = titulo;
        this.precio = precio;
        this.existencias = existencias;
        
    }
    
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public double getPrecio() { return precio; }
    public int getExistencias() { return existencias; }
    
    public void setPrecio(double precio) { this.precio = precio; }
    public void setExistencias(int existencias) { this.existencias = existencias; }
    
    @Override
    public String toString() {
        
        return "ISBN: " + isbn + "  Título: " + titulo + "  Precio: " + precio + "  Stock: " + existencias;
        
    }
    
}
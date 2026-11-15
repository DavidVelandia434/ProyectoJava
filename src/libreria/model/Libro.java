/*
 * Programa : Sistema de gestión de libreria
 * Archivo  : Libro.java
 * Fecha    : 02/11/2026
 */
package libreria.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Libro del catálogo.
 * La relación con Autor es de MUCHOS A MUCHOS (un libro puede tener varios
 * autores y un autor varios libros), por eso se guarda una lista de ids de
 * autores. En SQL esto se resolverá con la tabla intermedia libro_autor.
 */
public class Libro {
    private Integer id;
    private String isbn;
    private String titulo;
    private BigDecimal precio;
    private int stock;
    private Integer idCategoria;
    private Integer idProveedor;
    private List<Integer> idsAutores = new ArrayList<>();

    public Libro(Integer id, String isbn, String titulo, BigDecimal precio, int stock,
            Integer idCategoria, Integer idProveedor, List<Integer> idsAutores) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        if (idsAutores != null) {
            this.idsAutores = new ArrayList<>(idsAutores);
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }
    public List<Integer> getIdsAutores() { return new ArrayList<>(idsAutores); }
    public void setIdsAutores(List<Integer> idsAutores) { this.idsAutores = new ArrayList<>(idsAutores); }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " | ISBN: " + isbn + " | $" + precio
                + " | Stock: " + stock + " | Cat: " + idCategoria;
    }
}

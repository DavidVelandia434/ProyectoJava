/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MenuLibros.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

import libreria.model.Libro;
import libreria.service.AutorService;
import libreria.service.CategoriaService;
import libreria.service.LibroService;
import libreria.service.ProveedorService;
import libreria.service.ReglaNegocioException;
import libreria.dao.PersistenciaException;

public class MenuLibros {
    private final ConsolaUtil c;
    private final LibroService libros;
    private final CategoriaService categorias;
    private final ProveedorService proveedores;
    private final AutorService autores;

    public MenuLibros(ConsolaUtil c, LibroService libros, CategoriaService categorias,
                      ProveedorService proveedores, AutorService autores) {
        this.c = c;
        this.libros = libros;
        this.categorias = categorias;
        this.proveedores = proveedores;
        this.autores = autores;
    }

    public void ejecutar() {
        int op;
        do {
            ConsolaUtil.titulo("LIBROS");
            System.out.println("1. Listar por título\n2. Listar por categoría\n3. Listar por rango de precio\n"
                    + "4. Existencias bajas\n5. Registrar libro\n6. Actualizar precio\n7. Corregir título\n"
                    + "8. Ajustar existencias\n9. Eliminar libro\n0. Volver");
            op = c.leerEntero("Opción:");
            try {
                switch (op) {
                    case 1: ConsolaUtil.imprimirLista(libros.listarPorTitulo()); break;
                    case 2:
                        ConsolaUtil.imprimirLista(categorias.listar());
                        ConsolaUtil.imprimirLista(libros.listarPorCategoria(c.leerEntero("Id de la categoría:")));
                        break;
                    case 3:
                        ConsolaUtil.imprimirLista(libros.listarPorRangoPrecio(
                                c.leerDecimal("Precio mínimo:"), c.leerDecimal("Precio máximo:")));
                        break;
                    case 4: ConsolaUtil.imprimirLista(libros.listarStockBajo(c.leerEntero("Umbral de existencias:"))); break;
                    case 5: registrar(); break;
                    case 6:
                        libros.actualizarPrecio(c.leerEntero("Id del libro:"), c.leerDecimal("Nuevo precio:"));
                        System.out.println("Precio actualizado correctamente.");
                        break;
                    case 7:
                        libros.corregirTitulo(c.leerEntero("Id del libro:"), c.leerTexto("Nuevo título:"));
                        System.out.println("Título corregido correctamente.");
                        break;
                    case 8:
                        int id = c.leerEntero("Id del libro:");
                        int nuevo = c.leerEntero("Nuevas existencias:");
                        String motivo = c.leerTexto("Motivo del ajuste:");
                        if (c.confirmar("¿Confirmar el ajuste de existencias?")) {
                            libros.ajustarStock(id, nuevo, motivo);
                            System.out.println("Existencias actualizadas correctamente.");
                        } else {
                            System.out.println("Operación cancelada.");
                        }
                        break;
                    case 9:
                        int idEliminar = c.leerEntero("Id del libro a eliminar:");
                        if (c.confirmar("¿Seguro que desea eliminar el libro?")) {
                            System.out.println(libros.eliminar(idEliminar).getMensaje());
                        } else {
                            System.out.println("Operación cancelada.");
                        }
                        break;
                    case 0: break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (ReglaNegocioException | PersistenciaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    private void registrar() {
        ConsolaUtil.imprimirLista(categorias.listar());
        ConsolaUtil.imprimirLista(proveedores.listar());
        ConsolaUtil.imprimirLista(autores.listar());
        String isbn = c.leerTexto("ISBN:");
        String titulo = c.leerTexto("Título:");
        java.math.BigDecimal precio = c.leerDecimal("Precio:");
        int stock = c.leerEntero("Existencias:");
        int idCat = c.leerEntero("Id de la categoría:");
        int idProv = c.leerEntero("Id del proveedor:");
        java.util.List<Integer> ids = c.leerListaEnteros("Ids de los autores");
        Libro l = libros.registrar(isbn, titulo, precio, stock, idCat, idProv, ids);
        System.out.println("Libro registrado: " + l);
    }
}

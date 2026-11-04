/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MenuCatalogo.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

import libreria.dao.PersistenciaException;
import libreria.service.AutorService;
import libreria.service.CategoriaService;
import libreria.service.ProveedorService;
import libreria.service.ReglaNegocioException;

/** Administración de autores, categorías y proveedores (RF02). */
public class MenuCatalogo {
    private final ConsolaUtil c;
    private final AutorService autores;
    private final CategoriaService categorias;
    private final ProveedorService proveedores;

    public MenuCatalogo(ConsolaUtil c, AutorService autores, CategoriaService categorias,
                        ProveedorService proveedores) {
        this.c = c;
        this.autores = autores;
        this.categorias = categorias;
        this.proveedores = proveedores;
    }

    public void ejecutar() {
        int op;
        do {
            ConsolaUtil.titulo("AUTORES, CATEGORÍAS Y PROVEEDORES");
            System.out.println("1. Listar autores\n2. Registrar autor\n3. Eliminar autor\n"
                    + "4. Listar categorías\n5. Registrar categoría\n6. Eliminar categoría\n"
                    + "7. Listar proveedores\n8. Registrar proveedor\n9. Actualizar datos de proveedor\n"
                    + "10. Eliminar proveedor\n0. Volver");
            op = c.leerEntero("Opción:");
            try {
                switch (op) {
                    case 1: ConsolaUtil.imprimirLista(autores.listar()); break;
                    case 2:
                        System.out.println("Registrado: " + autores.registrar(
                                c.leerTexto("Nombre:"), c.leerTexto("Nacionalidad:")));
                        break;
                    case 3: eliminarAutor(); break;
                    case 4: ConsolaUtil.imprimirLista(categorias.listar()); break;
                    case 5: System.out.println("Registrada: " + categorias.registrar(c.leerTexto("Nombre:"))); break;
                    case 6: eliminarCategoria(); break;
                    case 7: ConsolaUtil.imprimirLista(proveedores.listar()); break;
                    case 8:
                        System.out.println("Registrado: " + proveedores.registrar(
                                c.leerTexto("Nombre:"), c.leerTexto("Teléfono:"), c.leerTexto("Correo:")));
                        break;
                    case 9:
                        proveedores.actualizarDatos(c.leerEntero("Id del proveedor:"), c.leerTexto("Nombre:"),
                                c.leerTexto("Teléfono:"), c.leerTexto("Correo:"));
                        System.out.println("Proveedor actualizado correctamente.");
                        break;
                    case 10: eliminarProveedor(); break;
                    case 0: break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (ReglaNegocioException | PersistenciaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    private void eliminarAutor() {
        int id = c.leerEntero("Id del autor a eliminar:");
        System.out.println(c.confirmar("¿Seguro que desea eliminarlo?")
                ? autores.eliminar(id).getMensaje() : "Operación cancelada.");
    }

    private void eliminarCategoria() {
        int id = c.leerEntero("Id de la categoría a eliminar:");
        System.out.println(c.confirmar("¿Seguro que desea eliminarla?")
                ? categorias.eliminar(id).getMensaje() : "Operación cancelada.");
    }

    private void eliminarProveedor() {
        int id = c.leerEntero("Id del proveedor a eliminar:");
        System.out.println(c.confirmar("¿Seguro que desea eliminarlo?")
                ? proveedores.eliminar(id).getMensaje() : "Operación cancelada.");
    }
}

/*
 * Programa : Sistema de gestión de librería
 * Archivo  : MenuClientes.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

import libreria.dao.PersistenciaException;
import libreria.service.ClienteService;
import libreria.service.ReglaNegocioException;

public class MenuClientes {
    private final ConsolaUtil c;
    private final ClienteService clientes;

    public MenuClientes(ConsolaUtil c, ClienteService clientes) {
        this.c = c;
        this.clientes = clientes;
    }

    public void ejecutar() {
        int op;
        do {
            ConsolaUtil.titulo("CLIENTES");
            System.out.println("1. Listar\n2. Registrar\n3. Actualizar datos de contacto\n4. Eliminar\n0. Volver");
            op = c.leerEntero("Opción:");
            try {
                switch (op) {
                    case 1: ConsolaUtil.imprimirLista(clientes.listar()); break;
                    case 2:
                        System.out.println("Registrado: " + clientes.registrar(c.leerTexto("Nombre:"),
                                c.leerTexto("Documento:"), c.leerTexto("Teléfono:"), c.leerTexto("Correo:")));
                        break;
                    case 3:
                        clientes.actualizarContacto(c.leerEntero("Id del cliente:"), c.leerTexto("Nombre:"),
                                c.leerTexto("Teléfono:"), c.leerTexto("Correo:"));
                        System.out.println("Cliente actualizado correctamente.");
                        break;
                    case 4:
                        int id = c.leerEntero("Id del cliente a eliminar:");
                        System.out.println(c.confirmar("¿Seguro que desea eliminarlo?")
                                ? clientes.eliminar(id).getMensaje() : "Operación cancelada.");
                        break;
                    case 0: break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (ReglaNegocioException | PersistenciaException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }
}

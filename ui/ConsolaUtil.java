/*
 * Programa : Sistema de gestión de librería
 * Archivo  : ConsolaUtil.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Entrada y salida por consola con validación básica de formato. */
public class ConsolaUtil {
    private final Scanner in;

    public ConsolaUtil(Scanner in) {
        this.in = in;
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje + " ");
        return in.nextLine().trim();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    public BigDecimal leerDecimal(String mensaje) {
        while (true) {
            try {
                return new BigDecimal(leerTexto(mensaje).replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido (ej. 45000 o 45000.50).");
            }
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leerTexto(mensaje + " (AAAA-MM-DD)"));
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Use el formato AAAA-MM-DD.");
            }
        }
    }

    public List<Integer> leerListaEnteros(String mensaje) {
        while (true) {
            try {
                List<Integer> lista = new ArrayList<>();
                for (String parte : leerTexto(mensaje + " (separados por coma)").split(",")) {
                    lista.add(Integer.parseInt(parte.trim()));
                }
                return lista;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese números separados por coma, ej. 1,2.");
            }
        }
    }

    /** RF10: confirmación antes de operaciones críticas. */
    public boolean confirmar(String mensaje) {
        String r = leerTexto(mensaje + " (s/n)");
        return r.equalsIgnoreCase("s") || r.equalsIgnoreCase("si") || r.equalsIgnoreCase("sí");
    }

    public static void titulo(String texto) {
        System.out.println("\n=== " + texto + " ===");
    }

    public static <T> void imprimirLista(List<T> lista) {
        if (lista.isEmpty()) {
            System.out.println("(sin resultados)");
        } else {
            lista.forEach(System.out::println);
        }
    }
}

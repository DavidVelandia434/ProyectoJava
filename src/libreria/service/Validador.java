/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Validador.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.service;

import java.math.BigDecimal;

/** Validaciones reutilizables antes de persistir datos. */
final class Validador {
    private Validador() { }

    static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo '" + campo + "' es obligatorio.");
        }
        return valor.trim();
    }

    static String correoValido(String correo) {
        String c = textoObligatorio(correo, "correo");
        if (!c.contains("@") || c.startsWith("@") || c.endsWith("@")) {
            throw new ReglaNegocioException("El correo no tiene un formato válido.");
        }
        return c;
    }

    static BigDecimal precioPositivo(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El precio debe ser mayor que cero.");
        }
        return precio;
    }

    static int noNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new ReglaNegocioException("El campo '" + campo + "' no puede ser negativo.");
        }
        return valor;
    }
}

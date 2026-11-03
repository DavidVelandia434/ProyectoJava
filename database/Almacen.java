/*
 * Programa : Sistema de gestión de librería
 * Archivo  : Almacen.java
 * Fecha    : 02/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.database;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Emula una "tabla": guarda las filas por id y genera ids autoincrementales. */
public class Almacen<T> {
    private final Map<Integer, T> filas = new LinkedHashMap<>();
    private final AtomicInteger secuencia = new AtomicInteger(0);

    public int siguienteId() {
        return secuencia.incrementAndGet();
    }

    public Map<Integer, T> filas() {
        return filas;
    }
}

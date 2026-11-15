package libreria.database;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

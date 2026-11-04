/*
 * Programa : Sistema de gestión de librería
 * Archivo  : LibreriaApiApplication.java
 * Fecha    : 03/11/2026
 * Autor    : David Andrés Velandia Mendoza
 */
package libreria.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de la API REST. (La versión de consola sigue siendo libreria.Main.) */
@SpringBootApplication
public class LibreriaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibreriaApiApplication.class, args);
    }
}
